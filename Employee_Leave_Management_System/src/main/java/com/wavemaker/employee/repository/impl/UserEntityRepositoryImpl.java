package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.repository.UserEntityRepository;
import com.wavemaker.employee.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.*;

public class UserEntityRepositoryImpl implements UserEntityRepository {
    private static final String SEARCH_USER = "SELECT * FROM EMPLOYEE_PASSWORD WHERE EMAIL_ID = ? AND PASSWORD = ?";
    private static final String GET_USER_BY_ID_SQL = "SELECT EMPLOYEE_PASSWORD_ID, EMAIL_ID, PASSWORD FROM EMPLOYEE_PASSWORD WHERE EMP_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO EMPLOYEE_PASSWORD (EMP_ID, EMAIL_ID, PASSWORD) VALUES (?, ?, ?)";

    private static Connection connection;

    public UserEntityRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    @Override
    public UserEntity authenticateUser(UserEntity userEntity) throws ServerUnavilableException {
        String email = userEntity.getEmail();
        String password = userEntity.getPassword();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_USER);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userEntity.setUserId(resultSet.getInt("EMPLOYEE_PASSWORD_ID"));
                userEntity.setEmpId(resultSet.getInt("EMP_ID"));
                userEntity.setEmail(email);
                userEntity.setPassword(password);
                return userEntity;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to authenticate the user", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserEntity getUserEntityById(int empId) throws ServerUnavilableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_ID_SQL);
            preparedStatement.setInt(1, empId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setEmpId(empId);
                    userEntity.setUserId(resultSet.getInt("EMPLOYEE_PASSWORD_ID")); // Assuming 'userId' maps to 'EMPLOYEE_PASSWORD_ID'
                    userEntity.setEmail(resultSet.getString("EMAIL_ID"));
                    userEntity.setPassword(resultSet.getString("PASSWORD"));

                    return userEntity;
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Failed to retrieve user entity by ID.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    @Override
    public UserEntity addUserEntity(UserEntity userEntity) throws ServerUnavilableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userEntity.getUserId()); // Assuming 'userId' maps to 'EMP_ID'
            preparedStatement.setString(2, userEntity.getEmail());
            preparedStatement.setString(3, userEntity.getPassword());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new ServerUnavilableException("Creating user failed, no rows affected.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            // Retrieve the auto-generated user ID
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    userEntity.setUserId(resultSet.getInt(1)); // Assuming the ID generated is mapped to 'EMP_ID'
                } else {
                    throw new ServerUnavilableException("Creating user failed, no ID obtained.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }

            return userEntity;
        } catch (Exception e) {
            throw new ServerUnavilableException("Server is unavailable. Unable to add user entity.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
