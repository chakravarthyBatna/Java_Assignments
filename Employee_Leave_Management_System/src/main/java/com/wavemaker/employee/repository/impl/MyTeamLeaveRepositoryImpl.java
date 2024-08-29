package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.dto.LeaveRequestVO;
import com.wavemaker.employee.repository.MyTeamLeaveRepository;
import com.wavemaker.employee.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyTeamLeaveRepositoryImpl implements MyTeamLeaveRepository {
    private static final Logger logger = LoggerFactory.getLogger(MyTeamLeaveRepositoryImpl.class);
    private Connection connection;

    String GET_TEAM_LEAVE_REQUESTS =  "SELECT lr.LEAVE_REQUEST_ID, lr.EMP_ID, e.NAME, lr.LEAVE_TYPE_ID, lt.LEAVE_TYPE, lr.LEAVE_REASON, " +
            "lr.FROM_DATE, lr.TO_DATE, lr.DATE_OF_APPLICATION, lr.LEAVE_STATUS, lr.DATE_OF_ACTION " +
            "FROM LEAVE_REQUEST lr " +
            "JOIN EMPLOYEE e ON lr.EMP_ID = e.EMP_ID " +
            "JOIN LEAVE_TYPE lt ON lr.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID " +
            "WHERE e.MANAGER_ID = ? ";
    // Query with status filter
    private static final String STATUS_FILTER_QUERY =
            " AND lr.LEAVE_STATUS = ? ";

    // Complete query with ORDER BY clause
    private static final String ORDER_BY_QUERY =
            "ORDER BY CASE WHEN lr.LEAVE_STATUS = 'PENDING' THEN 1 ELSE 2 END, lr.FROM_DATE ASC";


    public MyTeamLeaveRepositoryImpl() {
        try {
            connection = DBConnector.getConnectionInstance();
        } catch (SQLException e) {
        }
    }

    public List<LeaveRequestVO> getMyTeamLeaveRequests(int empId, String status) throws ServerUnavilableException {
        List<LeaveRequestVO> leaveRequestVOList = new ArrayList<>();

        // Build the final query
        StringBuilder queryBuilder = new StringBuilder(GET_TEAM_LEAVE_REQUESTS);

        if (!"ALL".equalsIgnoreCase(status)) {
            queryBuilder.append(STATUS_FILTER_QUERY);
        }
        queryBuilder.append(ORDER_BY_QUERY);

        String query = queryBuilder.toString();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, empId);

            // Set the LEAVE_STATUS parameter if it's not "ALL"
            if (!"ALL".equalsIgnoreCase(status)) {
                preparedStatement.setString(2, status.toUpperCase());
            }
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    LeaveRequestVO leaveRequestVO = new LeaveRequestVO();
                    leaveRequestVO.setLeaveRequestId(rs.getInt("LEAVE_REQUEST_ID"));
                    leaveRequestVO.setEmpId(rs.getInt("EMP_ID"));
                    leaveRequestVO.setEmpName(rs.getString("NAME"));
                    leaveRequestVO.setLeaveTypeId(rs.getInt("LEAVE_TYPE_ID"));
                    leaveRequestVO.setLeaveType(rs.getString("LEAVE_TYPE")); // Set the leave type
                    leaveRequestVO.setLeaveReason(rs.getString("LEAVE_REASON"));
                    leaveRequestVO.setFromDate(rs.getDate("FROM_DATE"));
                    leaveRequestVO.setToDate(rs.getDate("TO_DATE"));
                    leaveRequestVO.setDateOfApplication(rs.getDate("DATE_OF_APPLICATION"));
                    leaveRequestVO.setLeaveStatus(LeaveRequestStatus.valueOf(rs.getString("LEAVE_STATUS")));
                    leaveRequestVO.setDateOfApproved(rs.getDate("DATE_OF_ACTION"));

                    leaveRequestVOList.add(leaveRequestVO);
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to retrieve leave requests for team members.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveRequestVOList;
    }

    @Override
    public boolean approveOrRejectTeamLeaveRequest(int leaveRequestId, int approvingEmpId, LeaveRequestStatus approveOrRejectOrCancel) throws ServerUnavilableException {
        String query = null;

        if (approveOrRejectOrCancel == LeaveRequestStatus.CANCELLED) {
            query = "UPDATE LEAVE_REQUEST "
                    + "SET LEAVE_STATUS = ?, DATE_OF_ACTION = ? "
                    + "WHERE LEAVE_REQUEST_ID = ? AND EMP_ID = ?";
        } else {
            query = "UPDATE LEAVE_REQUEST lr "
                    + "JOIN EMPLOYEE e ON lr.EMP_ID = e.EMP_ID "
                    + "SET lr.LEAVE_STATUS = ?, lr.DATE_OF_ACTION = ? "
                    + "WHERE lr.LEAVE_REQUEST_ID = ? AND e.MANAGER_ID = ?";
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, approveOrRejectOrCancel.name()); // Set the leave status (APPROVED, UNAPPROVED, PENDING, CANCELLED)
            preparedStatement.setDate(2, new java.sql.Date(System.currentTimeMillis())); // Set the current date as DATE_OF_ACTION
            preparedStatement.setInt(3, leaveRequestId); // Set the leave request ID

            if (approveOrRejectOrCancel == LeaveRequestStatus.CANCELLED) {
                preparedStatement.setInt(4, approvingEmpId);
            } else {
                preparedStatement.setInt(4, approvingEmpId);
            }

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to update leave request status.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
