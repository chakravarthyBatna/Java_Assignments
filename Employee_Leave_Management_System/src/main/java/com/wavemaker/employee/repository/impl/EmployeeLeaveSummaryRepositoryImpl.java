package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.EmployeeLeaveSummary;
import com.wavemaker.employee.pojo.dto.EmployeeIdNameVO;
import com.wavemaker.employee.repository.EmployeeLeaveSummaryRepository;
import com.wavemaker.employee.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeLeaveSummaryRepositoryImpl implements EmployeeLeaveSummaryRepository {
    private static final String FETCH_LEAVE_REQUESTS_QUERY =
            "SELECT LEAVE_TYPE_ID, TOTAL_DAYS " +
                    "FROM LEAVE_REQUEST " +
                    "WHERE EMP_ID = ? AND LEAVE_STATUS = 'APPROVED'";
    private static final String UPDATE_SUMMARY_QUERY =
            "UPDATE EMPLOYEE_LEAVE_SUMMARY " +
                    "SET TOTAL_LEAVES_TAKEN = ?, " +
                    " PENDING_LEAVES = (SELECT MAX_LEAVE_DAYS_ALLOWED " +
                    "FROM LEAVE_TYPE WHERE LEAVE_TYPE_ID = ?) - ? " +
                    "WHERE EMP_ID = ? AND LEAVE_TYPE_ID = ?";
    private static final String SQL_SELECT_EMPLOYEE_LEAVE_SUMMARY =
            "SELECT SUMMARY_ID, EMP_ID, LEAVE_TYPE_ID, LEAVE_TYPE, PENDING_LEAVES, TOTAL_LEAVES_TAKEN, LAST_UPDATED " +
                    "FROM employee_leave_summary " +
                    "WHERE EMP_ID = ?";
    private static final String EMPLOYEES_QUERY = "SELECT `EMP_ID`, `NAME` " +
            "FROM `employee` " +
            "WHERE `MANAGER_ID` = ?";
    private static final String LEAVE_SUMMARIES_QUERY = "SELECT `SUMMARY_ID`, `EMP_ID`, `LEAVE_TYPE_ID`, `LEAVE_TYPE`, `PENDING_LEAVES`, `TOTAL_LEAVES_TAKEN`, `LAST_UPDATED` " +
            "FROM `employee_leave_summary` " +
            "WHERE `EMP_ID` = ?";
    private Connection connection;

    public EmployeeLeaveSummaryRepositoryImpl() throws SQLException {
        this.connection = DBConnector.getConnectionInstance();
    }

    @Override
    public boolean updateEmployeeLeaveSummary(int empId) throws ServerUnavilableException {
        try {
            PreparedStatement fetchStmt = connection.prepareStatement(FETCH_LEAVE_REQUESTS_QUERY);
            fetchStmt.setInt(1, empId);
            ResultSet rs = fetchStmt.executeQuery();

            // 2. Calculate total leaves taken per leave type
            Map<Integer, Integer> leaveSummaryMap = new HashMap<>();

            while (rs.next()) {
                int leaveTypeId = rs.getInt("LEAVE_TYPE_ID");
                int totalDays = rs.getInt("TOTAL_DAYS");

                leaveSummaryMap.put(leaveTypeId, leaveSummaryMap.getOrDefault(leaveTypeId, 0) + totalDays);
            }
            PreparedStatement updateStmt = connection.prepareStatement(UPDATE_SUMMARY_QUERY);
            for (Map.Entry<Integer, Integer> entry : leaveSummaryMap.entrySet()) {
                int leaveTypeId = entry.getKey();
                int totalLeavesTaken = entry.getValue();

                updateStmt.setInt(1, totalLeavesTaken);
                updateStmt.setInt(2, leaveTypeId);
                updateStmt.setInt(3, totalLeavesTaken);
                updateStmt.setInt(4, empId);
                updateStmt.setInt(5, leaveTypeId);
                updateStmt.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            throw new ServerUnavilableException("Could not update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummariesById(int empId) throws ServerUnavilableException {
        List<EmployeeLeaveSummary> summaries = new ArrayList<>();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(SQL_SELECT_EMPLOYEE_LEAVE_SUMMARY);
            preparedStatement.setInt(1, empId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                EmployeeLeaveSummary summary = new EmployeeLeaveSummary();
                summary.summaryId = resultSet.getInt("SUMMARY_ID");
                summary.empId = resultSet.getInt("EMP_ID");
                summary.leaveTypeId = resultSet.getInt("LEAVE_TYPE_ID");
                summary.leaveType = resultSet.getString("LEAVE_TYPE");
                summary.pendingLeaves = resultSet.getInt("PENDING_LEAVES");
                summary.totalLeavesTaken = resultSet.getInt("TOTAL_LEAVES_TAKEN");
                summary.lastUpdated = resultSet.getTimestamp("LAST_UPDATED");

                summaries.add(summary); // Add each summary to the list
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error fetching employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return summaries;
    }

    @Override
    public Map<EmployeeIdNameVO, List<EmployeeLeaveSummary>> getAllEmployeesLeaveSummary(int managerId) throws ServerUnavilableException {
        try {
            PreparedStatement employeesStatement = connection.prepareStatement(EMPLOYEES_QUERY);
            employeesStatement.setInt(1, managerId);
            ResultSet employeesResultSet = employeesStatement.executeQuery();
            Map<EmployeeIdNameVO, List<EmployeeLeaveSummary>> resultMap = new HashMap<>();

            while (employeesResultSet.next()) {
                int empId = employeesResultSet.getInt("EMP_ID");
                String empName = employeesResultSet.getString("NAME");

                EmployeeIdNameVO empVO = new EmployeeIdNameVO();
                empVO.setEmpId(empId);
                empVO.setEmpName(empName);

                PreparedStatement leaveSummariesStatement = connection.prepareStatement(LEAVE_SUMMARIES_QUERY);
                leaveSummariesStatement.setInt(1, empId);
                ResultSet leaveSummariesResultSet = leaveSummariesStatement.executeQuery();

                List<EmployeeLeaveSummary> summaries = new ArrayList<>();
                while (leaveSummariesResultSet.next()) {
                    EmployeeLeaveSummary summary = new EmployeeLeaveSummary();
                    summary.summaryId = leaveSummariesResultSet.getInt("SUMMARY_ID");
                    summary.empId = empId;
                    summary.leaveTypeId = leaveSummariesResultSet.getInt("LEAVE_TYPE_ID");
                    summary.leaveType = leaveSummariesResultSet.getString("LEAVE_TYPE");
                    summary.pendingLeaves = leaveSummariesResultSet.getInt("PENDING_LEAVES");
                    summary.totalLeavesTaken = leaveSummariesResultSet.getInt("TOTAL_LEAVES_TAKEN");
                    summary.lastUpdated = leaveSummariesResultSet.getTimestamp("LAST_UPDATED");

                    summaries.add(summary);
                }

                resultMap.put(empVO, summaries);
            }

            return resultMap;
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error fetching all employee leave summaries", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
