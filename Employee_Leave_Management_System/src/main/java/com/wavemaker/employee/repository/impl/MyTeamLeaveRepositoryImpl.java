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

    private static final String SQL_UPDATE_LEAVE_REQUEST_APPROVE_REJECT =
            "UPDATE LEAVE_REQUEST lr "
                    + "JOIN EMPLOYEE e ON lr.EMP_ID = e.EMP_ID "
                    + "SET lr.LEAVE_STATUS = ?, lr.DATE_OF_ACTION = ? "
                    + "WHERE lr.LEAVE_REQUEST_ID = ? AND e.MANAGER_ID = ?";

    String GET_TEAM_LEAVE_REQUESTS =
            "SELECT lr.LEAVE_REQUEST_ID, lr.EMP_ID, e.NAME, lr.LEAVE_TYPE_ID, lt.LEAVE_TYPE, lr.LEAVE_REASON, " +
                    "lr.FROM_DATE, lr.TO_DATE, lr.DATE_OF_APPLICATION, lr.LEAVE_STATUS, lr.DATE_OF_ACTION, lr.TOTAL_DAYS " +
                    "FROM LEAVE_REQUEST lr " +
                    "JOIN EMPLOYEE e ON lr.EMP_ID = e.EMP_ID " +
                    "JOIN LEAVE_TYPE lt ON lr.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID " +
                    "WHERE e.MANAGER_ID = ? ";

    String STATUS_FILTER_QUERY =
            " AND lr.LEAVE_STATUS = ? ";

    String ORDER_BY_QUERY =
            " ORDER BY lr.DATE_OF_APPLICATION DESC";

    private Connection connection;


    public MyTeamLeaveRepositoryImpl() {
        try {
            connection = DBConnector.getConnectionInstance();
        } catch (SQLException e) {
        }
    }

    public List<LeaveRequestVO> getMyTeamLeaveRequests(int empId, String status) throws ServerUnavilableException {
        List<LeaveRequestVO> leaveRequestVOList = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder(GET_TEAM_LEAVE_REQUESTS);

        if ("ALL_EXCLUDE_PENDING".equalsIgnoreCase(status)) {
            queryBuilder.append(" AND lr.LEAVE_STATUS != 'PENDING'");
        }
        else if (!"ALL".equalsIgnoreCase(status)) {
            queryBuilder.append(STATUS_FILTER_QUERY);
        }

        // Add the ORDER BY clause
        queryBuilder.append(ORDER_BY_QUERY);

        String query = queryBuilder.toString();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, empId);

            if (!"ALL".equalsIgnoreCase(status) && !"ALL_EXCLUDE_PENDING".equalsIgnoreCase(status)) {
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
                    leaveRequestVO.setTotalNoOfDays(rs.getInt("TOTAL_DAYS"));
                    leaveRequestVOList.add(leaveRequestVO);
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to retrieve leave requests for team members.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveRequestVOList;
    }


    @Override
    public boolean approveOrRejectTeamLeaveRequest(int leaveRequestId, int approvingEmpId, LeaveRequestStatus approveOrReject) throws ServerUnavilableException {
        if (approveOrReject != LeaveRequestStatus.APPROVED && approveOrReject != LeaveRequestStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid status. Only APPROVED and REJECTED are allowed.");
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_LEAVE_REQUEST_APPROVE_REJECT)) {
            preparedStatement.setString(1, approveOrReject.name());
            preparedStatement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            preparedStatement.setInt(3, leaveRequestId);
            preparedStatement.setInt(4, approvingEmpId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to update leave request status.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
