package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.LeaveType;
import com.wavemaker.employee.repository.LeaveTypeRepository;
import com.wavemaker.employee.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaveTypeRepositoryImpl implements LeaveTypeRepository {
    private static Connection connection;

    private static final String SELECT_LEAVE_TYPE_QUERY = "SELECT LEAVE_TYPE_ID, LEAVE_TYPE, DESCRIPTION, MAX_LEAVE_DAYS_ALLOWED, APPLICABLE_GENDER " +
            "FROM LEAVE_TYPE " +
            "WHERE APPLICABLE_GENDER = 'BOTH' " +
            "OR APPLICABLE_GENDER = ?";

    private static final String QUERY_GET_MAX_DAYS_ALLOWED = "SELECT MAX_LEAVE_DAYS_ALLOWED " +
            "FROM leave_type WHERE LEAVE_TYPE_ID = ?";

    public LeaveTypeRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    @Override
    public List<LeaveType> getAllLeaveTypes(String gender) throws ServerUnavilableException {
        List<LeaveType> leaveTypes = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LEAVE_TYPE_QUERY)) {
            // Set the parameter based on the provided gender
            preparedStatement.setString(1, gender.toUpperCase());

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    LeaveType leaveType = new LeaveType();
                    leaveType.setLeaveTypeId(rs.getInt("LEAVE_TYPE_ID"));
                    leaveType.setLeaveType(rs.getString("LEAVE_TYPE"));
                    leaveType.setDescription(rs.getString("DESCRIPTION"));
                    leaveType.setMaxNoOfLeaves(rs.getInt("MAX_LEAVE_DAYS_ALLOWED"));
                    leaveType.setApplicableForGender(rs.getString("APPLICABLE_GENDER"));

                    leaveTypes.add(leaveType);
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to retrieve leave type information", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveTypes;
    }

    @Override
    public boolean isLeaveTypeWithInRange(int leaveTypeId, int totalDays) throws ServerUnavilableException, LeaveDaysExceededException {

        try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_GET_MAX_DAYS_ALLOWED)) {
            preparedStatement.setInt(1, leaveTypeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int maxAllowedDays = resultSet.getInt("MAX_LEAVE_DAYS_ALLOWED");
                    return totalDays < maxAllowedDays;
                } else {
                    throw new IllegalArgumentException("Leave type ID not found.");
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Failed to retrieve maximum allowed days for leave type.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


}
