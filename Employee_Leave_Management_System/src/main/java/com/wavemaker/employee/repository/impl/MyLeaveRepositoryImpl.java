package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.dto.EmployeeLeaveRequestVO;
import com.wavemaker.employee.repository.MyLeaveRepository;
import com.wavemaker.employee.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyLeaveRepositoryImpl implements MyLeaveRepository {

    private Connection connection;

    private static final String INSERT_LEAVE_REQUEST_QUERY = "INSERT INTO LEAVE_REQUEST" +
            " (EMP_ID, LEAVE_TYPE_ID, LEAVE_REASON, FROM_DATE, TO_DATE, DATE_OF_APPLICATION, " +
            "LEAVE_STATUS, DATE_OF_ACTION, TOTAL_DAYS) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

    private static final String GET_ALL_LEAVE_REQUESTS =
            "SELECT lr.LEAVE_REQUEST_ID, lr.EMP_ID, e.NAME AS EMP_NAME, lt.LEAVE_TYPE, lr.LEAVE_REASON, " +
                    "       lr.FROM_DATE, lr.TO_DATE, lr.DATE_OF_APPLICATION, lr.LEAVE_STATUS, lr.DATE_OF_ACTION, lr.TOTAL_DAYS " +
                    "FROM LEAVE_REQUEST lr " +
                    "JOIN EMPLOYEE e ON lr.EMP_ID = e.EMP_ID " +
                    "JOIN LEAVE_TYPE lt ON lr.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID " +
                    "WHERE lr.EMP_ID = ? " +
                    "ORDER BY " +
                    "    CASE " +
                    "        WHEN lr.LEAVE_STATUS = 'PENDING' THEN 0 " +
                    "        ELSE 1 " +
                    "    END, " +
                    "    lr.DATE_OF_APPLICATION DESC";

    private static final String GET_LEAVE_REQUESTS_BY_STATUS =
            "SELECT lr.LEAVE_REQUEST_ID, lr.EMP_ID, e.NAME AS EMP_NAME, lt.LEAVE_TYPE, lr.LEAVE_REASON, " +
                    "       lr.FROM_DATE, lr.TO_DATE, lr.DATE_OF_APPLICATION, lr.LEAVE_STATUS, lr.DATE_OF_ACTION, lr.TOTAL_DAYS " +
                    "FROM LEAVE_REQUEST lr " +
                    "JOIN EMPLOYEE e ON lr.EMP_ID = e.EMP_ID " +
                    "JOIN LEAVE_TYPE lt ON lr.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID " +
                    "WHERE lr.EMP_ID = ? AND lr.LEAVE_STATUS = ? " +
                    "ORDER BY lr.DATE_OF_APPLICATION DESC";

    private static final String GET_ALL_LEAVE_REQUESTS_EXCLUDE_PENDING =
            "SELECT lr.LEAVE_REQUEST_ID, lr.EMP_ID, e.NAME AS EMP_NAME, lt.LEAVE_TYPE, lr.LEAVE_REASON, " +
                    "       lr.FROM_DATE, lr.TO_DATE, lr.DATE_OF_APPLICATION, lr.LEAVE_STATUS, lr.DATE_OF_ACTION, lr.TOTAL_DAYS " +
                    "FROM LEAVE_REQUEST lr " +
                    "JOIN EMPLOYEE e ON lr.EMP_ID = e.EMP_ID " +
                    "JOIN LEAVE_TYPE lt ON lr.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID " +
                    "WHERE lr.EMP_ID = ? AND lr.LEAVE_STATUS != 'PENDING' " +
                    "ORDER BY lr.DATE_OF_APPLICATION DESC";


    private static final String UPDATE_LEAVE_REQUEST_SQL =
            "UPDATE leave_request SET EMP_ID = ?, LEAVE_TYPE_ID = ?, LEAVE_REASON = ?, " +
                    "FROM_DATE = ?, TO_DATE = ?, DATE_OF_APPLICATION = ?, LEAVE_STATUS = ?, " +
                    "DATE_OF_ACTION = ?, TOTAL_DAYS = ? WHERE LEAVE_REQUEST_ID = ?";

    private static final String SQL_SELECT_EMP_ID_BY_LEAVE_REQUEST_ID =
            "SELECT EMP_ID FROM leave_request WHERE LEAVE_REQUEST_ID = ?";

    public MyLeaveRepositoryImpl() throws SQLException {
        this.connection = DBConnector.getConnectionInstance();
    }

    @Override
    public LeaveRequest applyForLeave(LeaveRequest leaveRequest) throws ServerUnavilableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LEAVE_REQUEST_QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, leaveRequest.getEmpId());
            preparedStatement.setInt(2, leaveRequest.getLeaveTypeId());
            preparedStatement.setString(3, leaveRequest.getLeaveReason());
            preparedStatement.setDate(4, new java.sql.Date(leaveRequest.getFromDate().getTime()));
            preparedStatement.setDate(5, new java.sql.Date(leaveRequest.getToDate().getTime()));
            preparedStatement.setDate(6, new java.sql.Date(leaveRequest.getDateOfApplication().getTime()));
            preparedStatement.setString(7, leaveRequest.getLeaveStatus().name());
            preparedStatement.setDate(8, leaveRequest.getDateOfApproved() != null ? new java.sql.Date(leaveRequest.getDateOfApproved().getTime()) : null);
            preparedStatement.setInt(9, leaveRequest.getTotalNoOfDays());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        leaveRequest.setLeaveRequestId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating leave request failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Unexpected exception", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return leaveRequest;
    }

    @Override
    public boolean cancelMyLeaveRequest(int leaveRequestId, int empId) throws ServerUnavilableException {
        String query = "UPDATE LEAVE_REQUEST "
                + "SET LEAVE_STATUS = ?, DATE_OF_ACTION = ? "
                + "WHERE LEAVE_REQUEST_ID = ? AND EMP_ID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, LeaveRequestStatus.CANCELLED.name()); // Set the leave status to CANCELLED
            preparedStatement.setDate(2, new java.sql.Date(System.currentTimeMillis())); // Set the current date as DATE_OF_ACTION
            preparedStatement.setInt(3, leaveRequestId); // Set the leave request ID
            preparedStatement.setInt(4, empId); // Set the employee ID (EMP_ID)

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to update leave request status.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public List<EmployeeLeaveRequestVO> getMyLeaveRequests(int empId, LeaveRequestStatus status) throws ServerUnavilableException {
        List<EmployeeLeaveRequestVO> leaveRequests = new ArrayList<>();
        String query;

        // Determine which query to use based on the status
        if (status == LeaveRequestStatus.ALL_EXCLUDE_PENDING) {
            query = GET_ALL_LEAVE_REQUESTS_EXCLUDE_PENDING;
        } else if (status == LeaveRequestStatus.ALL) {
            query = GET_ALL_LEAVE_REQUESTS;
        } else {
            query = GET_LEAVE_REQUESTS_BY_STATUS;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, empId);
            if (status != LeaveRequestStatus.ALL && status != LeaveRequestStatus.ALL_EXCLUDE_PENDING) {
                preparedStatement.setString(2, status.name());
            }

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    EmployeeLeaveRequestVO leaveRequest = new EmployeeLeaveRequestVO();
                    leaveRequest.setLeaveRequestId(rs.getInt("LEAVE_REQUEST_ID"));
                    leaveRequest.setLeaveType(rs.getString("LEAVE_TYPE")); // Adjusted column name
                    leaveRequest.setLeaveReason(rs.getString("LEAVE_REASON"));
                    leaveRequest.setFromDate(rs.getDate("FROM_DATE"));
                    leaveRequest.setToDate(rs.getDate("TO_DATE"));
                    leaveRequest.setDateOfApplication(rs.getDate("DATE_OF_APPLICATION"));
                    leaveRequest.setLeaveRequestStatus(LeaveRequestStatus.valueOf(rs.getString("LEAVE_STATUS")));
                    leaveRequest.setDateOfApproved(rs.getDate("DATE_OF_ACTION"));
                    leaveRequest.setTotalDays(rs.getInt("TOTAL_DAYS"));
                    leaveRequests.add(leaveRequest);
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to retrieve leave requests.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveRequests;
    }

    @Override
    public boolean updateMyLeaveRequest(LeaveRequest leaveRequest) throws ServerUnavilableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LEAVE_REQUEST_SQL);
            preparedStatement.setInt(1, leaveRequest.getEmpId());
            preparedStatement.setInt(2, leaveRequest.getLeaveTypeId());
            preparedStatement.setString(3, leaveRequest.getLeaveReason());
            preparedStatement.setDate(4, new Date(leaveRequest.getFromDate().getTime()));
            preparedStatement.setDate(5, new Date(leaveRequest.getToDate().getTime()));
            preparedStatement.setDate(6, new Date(leaveRequest.getDateOfApplication().getTime()));
            preparedStatement.setDate(8, new Date(leaveRequest.getDateOfApproved().getTime()));
            preparedStatement.setInt(9, leaveRequest.getLeaveRequestId());
            preparedStatement.setInt(10, leaveRequest.getTotalNoOfDays());
            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to Update leave requests.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public int getEmployeeIdByLeaveRequestId(int leaveRequestId) throws ServerUnavilableException {
        int empId = -1;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(SQL_SELECT_EMP_ID_BY_LEAVE_REQUEST_ID);
            preparedStatement.setInt(1, leaveRequestId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                empId = resultSet.getInt("EMP_ID");
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error fetching employee ID by leave request ID", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return empId;
    }
}
