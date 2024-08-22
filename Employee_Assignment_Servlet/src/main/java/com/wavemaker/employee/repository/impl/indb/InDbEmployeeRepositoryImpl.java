package com.wavemaker.employee.repository.impl.indb;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.exception.employee.EmployeeFileReadException;
import com.wavemaker.employee.exception.employee.EmployeeNotFoundException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.util.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InDbEmployeeRepositoryImpl implements EmployeeRepository {

    private static final Logger logger = LoggerFactory.getLogger(InDbEmployeeRepositoryImpl.class);

    private static final String SELECT_EMPLOYEE_BY_ID = "SELECT * FROM EMPLOYEE WHERE EMP_ID = ?";
    private static final String INSERT_EMPLOYEE = "INSERT INTO EMPLOYEE (NAME, GENDER, AGE, EMAIL) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_EMPLOYEES = "SELECT * FROM EMPLOYEE";
    private static final String UPDATE_EMPLOYEE = "UPDATE EMPLOYEE SET NAME = ?, GENDER = ?, AGE = ?, EMAIL = ? WHERE EMP_ID = ?";
    private static final String DELETE_EMPLOYEE = "DELETE FROM EMPLOYEE WHERE EMP_ID = ?";
    private static final String SELECT_EMPLOYEE_BY_EMAIL = "SELECT * FROM EMPLOYEE WHERE EMAIL = ?";
    private static final String CHECK_EMPLOYEE_EXISTS = "SELECT COUNT(*) FROM EMPLOYEE WHERE EMP_ID = ?";

    private static Connection connection = null;
    public InDbEmployeeRepositoryImpl() throws SQLException {
       connection = DBConnector.getConnectionInstance();

    }
    @Override
    public Employee getEmployeeById(int empId) throws ServerUnavilableException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_EMPLOYEE_BY_ID);
            preparedStatement.setInt(1, empId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToEmployee(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error reading employee from database", 500);
        } finally {
            closeResources(resultSet, preparedStatement);
        }
    }

    @Override
    public Employee addEmployee(Employee employee) throws ServerUnavilableException {
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;
        try {
            preparedStatement = connection.prepareStatement(INSERT_EMPLOYEE, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getGender());
            preparedStatement.setInt(3, employee.getAge());
            preparedStatement.setString(4, employee.getEmail());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    employee.setEmpId(generatedKeys.getInt(1));
                }
            } else {
                logger.warn("Employee not added");
            }
            return employee;
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error adding employee to database", 500);
        } finally {
            closeResources(generatedKeys, preparedStatement);
        }
    }

    @Override
    public List<Employee> getAllEmployeeDetails() throws EmployeeFileReadException {
        List<Employee> employees = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_ALL_EMPLOYEES);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employees.add(mapResultSetToEmployee(resultSet));
            }
        } catch (SQLException e) {
            throw new EmployeeFileReadException("Error reading all employees from database", 500);
        } finally {
            closeResources(resultSet, preparedStatement);
        }
        return employees;
    }

    @Override
    public Employee updateEmployee(Employee employee) throws EmployeeNotFoundException,ServerUnavilableException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(UPDATE_EMPLOYEE);
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getGender());
            preparedStatement.setInt(3, employee.getAge());
            preparedStatement.setString(4, employee.getEmail());
            preparedStatement.setInt(5, employee.getEmpId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return employee;
            } else {
                throw new EmployeeNotFoundException("Employee with ID " + employee.getEmpId() + " not found", 404);
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error updating employee in database", 500);
        } finally {
            closeResources(null, preparedStatement);
        }
    }

    @Override
    public Employee deleteEmployee(int empId) throws EmployeeNotFoundException, ServerUnavilableException {
        PreparedStatement preparedStatement = null;
        try {
            Employee employee = getEmployeeById(empId);
            if (employee == null) {
                throw new EmployeeNotFoundException("Employee with ID " + empId + " not found", 404);
            }
            preparedStatement = connection.prepareStatement(DELETE_EMPLOYEE);
            preparedStatement.setInt(1, empId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return employee;
            } else {
                throw new ServerUnavilableException("Error deleting employee from database", 500);
            }
        } catch (SQLException | ServerUnavilableException e) {
            throw new ServerUnavilableException("Error deleting employee from database", 500);
        } finally {
            closeResources(null, preparedStatement);
        }
    }

    @Override
    public Employee getEmployeeByEmail(String email) throws ServerUnavilableException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_EMPLOYEE_BY_EMAIL);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToEmployee(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error reading employee by email from database",500);
        } finally {
            closeResources(resultSet, preparedStatement);
        }
    }

    @Override
    public boolean isEmployeeExists(int empId) throws ServerUnavilableException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(CHECK_EMPLOYEE_EXISTS);
            preparedStatement.setInt(1, empId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error checking if employee exists", 500);
        } finally {
            closeResources(resultSet, preparedStatement);
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmpId(rs.getInt("EMP_ID"));
        employee.setName(rs.getString("NAME"));
        employee.setGender(rs.getString("GENDER"));
        employee.setAge(rs.getInt("AGE"));
        employee.setEmail(rs.getString("EMAIL"));
        return employee;
    }

    private void closeResources(ResultSet rs, PreparedStatement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
           logger.warn("Exception : ",e);
        }
    }
}
