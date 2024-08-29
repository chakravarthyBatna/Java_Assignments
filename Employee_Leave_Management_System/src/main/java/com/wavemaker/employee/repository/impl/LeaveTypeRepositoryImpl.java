package com.wavemaker.employee.repository.impl;

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

    public LeaveTypeRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    @Override
    public List<LeaveType> getAllLeaveTypes() throws ServerUnavilableException {
        List<LeaveType> leaveTypes = new ArrayList<>();
        String query = "SELECT LEAVE_TYPE_ID, LEAVE_TYPE, DESCRIPTION, MAX_LEAVE_DAYS_ALLOWED, APPLICABLE_GENDER FROM LEAVE_TYPE";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                LeaveType leaveType = new LeaveType();
                leaveType.setLeaveTypeId(rs.getInt("LEAVE_TYPE_ID"));
                leaveType.setLeaveType(rs.getString("LEAVE_TYPE"));
                leaveType.setDescription(rs.getString("DESCRIPTION"));
                leaveType.setMaxNoOfLeaves(rs.getInt("MAX_LEAVE_DAYS_ALLOWED"));
                leaveType.setApplicableForGender(rs.getString("APPLICABLE_GENDER"));

                leaveTypes.add(leaveType);
            }
        } catch (SQLException e) {
          throw new ServerUnavilableException("Unavle to retrieve leave type information", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveTypes;
    }
}
