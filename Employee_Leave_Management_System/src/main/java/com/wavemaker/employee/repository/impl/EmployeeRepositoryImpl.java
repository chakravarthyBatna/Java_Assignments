package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.dto.EmployeeVO;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    private static final String SELECT_EMPLOYEE_AND_MANAGER_QUERY =
            "SELECT e.EMP_ID, e.NAME, e.DOB, e.PHONE_NUMBER, e.EMAIL, e.MANAGER_ID, " +
                    "       e.GENDER, e.ROLE, " +
                    "       m.NAME AS MANAGER_NAME, m.PHONE_NUMBER AS MANAGER_PHONE_NUMBER, m.EMAIL AS MANAGER_EMAIL_ID " +
                    "FROM EMPLOYEE e " +
                    "LEFT JOIN EMPLOYEE m ON e.MANAGER_ID = m.EMP_ID " +
                    "WHERE e.EMP_ID = ?";
    private static final String ADD_EMPLOYEE_QUERY =
            "INSERT INTO EMPLOYEE (MANAGER_ID, NAME, DOB, PHONE_NUMBER, EMAIL) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_EMPLOYEE_QUERY =
            "UPDATE EMPLOYEE SET MANAGER_ID = ?, NAME = ?, DOB = ?, PHONE_NUMBER = ?, EMAIL = ? WHERE EMP_ID = ?";
    private static final String DELETE_EMPLOYEE_QUERY =
            "DELETE FROM EMPLOYEE WHERE EMP_ID = ?";
    private static final String GET_ALL_EMPLOYEES_QUERY =
            "SELECT EMP_ID, MANAGER_ID, NAME, DOB, PHONE_NUMBER, EMAIL FROM EMPLOYEE";
    private static final String GET_ALL_MANAGERS_QUERY =
            "SELECT DISTINCT e2.EMP_ID, e2.MANAGER_ID, e2.NAME, e2.DOB, e2.PHONE_NUMBER, e2.EMAIL " +
                    "FROM EMPLOYEE e1 " +
                    "JOIN EMPLOYEE e2 ON e1.MANAGER_ID = e2.EMP_ID";
    private Connection connection = null;

    public EmployeeRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    public EmployeeVO getEmployeeById(int empId) throws ServerUnavilableException {
        EmployeeVO employee = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EMPLOYEE_AND_MANAGER_QUERY);

            preparedStatement.setInt(1, empId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    employee = new EmployeeVO();
                    employee.setEmpId(resultSet.getInt("EMP_ID"));
                    employee.setEmpName(resultSet.getString("NAME"));
                    employee.setEmpDateOfBirth(resultSet.getDate("DOB"));
                    employee.setPhoneNumber(resultSet.getLong("PHONE_NUMBER"));
                    employee.setEmail(resultSet.getString("EMAIL"));
                    employee.setGender(resultSet.getString("GENDER"));
                    employee.setRole(resultSet.getString("ROLE"));
                    employee.setManagerId(resultSet.getInt("MANAGER_ID"));
                    employee.setManagerName(resultSet.getString("MANAGER_NAME"));
                    employee.setManagerPhoneNumber(resultSet.getString("MANAGER_PHONE_NUMBER"));
                    employee.setManagerEmailId(resultSet.getString("MANAGER_EMAIL_ID"));
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to get the Employee details", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return employee;
    }

    @Override
    public Employee addEmployee(Employee employee) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(ADD_EMPLOYEE_QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, employee.getManagerId());
            preparedStatement.setString(2, employee.getEmpName());
            preparedStatement.setDate(3, new java.sql.Date(employee.getEmpDateOfBirth().getTime()));
            preparedStatement.setLong(4, employee.getPhoneNumber());
            preparedStatement.setString(5, employee.getEmail());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setEmpId(generatedKeys.getInt(1));
                    }
                }
            }
            return employee;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EMPLOYEE_QUERY);
            preparedStatement.setInt(1, employee.getManagerId());
            preparedStatement.setString(2, employee.getEmpName());
            preparedStatement.setDate(3, new java.sql.Date(employee.getEmpDateOfBirth().getTime()));
            preparedStatement.setLong(4, employee.getPhoneNumber());
            preparedStatement.setString(5, employee.getEmail());
            preparedStatement.setInt(6, employee.getEmpId());
            preparedStatement.executeUpdate();
            return employee;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Employee deleteEmployee(int empId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_EMPLOYEE_QUERY);
            preparedStatement.setInt(1, empId);
            preparedStatement.executeUpdate();
            return null;  // Return the deleted employee information if needed.
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_EMPLOYEES_QUERY);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setEmpId(resultSet.getInt("EMP_ID"));
                employee.setManagerId(resultSet.getInt("MANAGER_ID"));
                employee.setEmpName(resultSet.getString("NAME"));
                employee.setEmpDateOfBirth(resultSet.getDate("DOB"));
                employee.setPhoneNumber(resultSet.getLong("PHONE_NUMBER"));
                employee.setEmail(resultSet.getString("EMAIL"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public List<Employee> getAllManagers() {
        List<Employee> managers = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_MANAGERS_QUERY);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Employee manager = new Employee();
                manager.setEmpId(resultSet.getInt("EMP_ID"));
                manager.setManagerId(resultSet.getInt("MANAGER_ID"));
                manager.setEmpName(resultSet.getString("NAME"));
                manager.setEmpDateOfBirth(resultSet.getDate("DOB"));
                manager.setPhoneNumber(resultSet.getLong("PHONE_NUMBER"));
                manager.setEmail(resultSet.getString("EMAIL"));
                managers.add(manager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return managers;
    }
}

