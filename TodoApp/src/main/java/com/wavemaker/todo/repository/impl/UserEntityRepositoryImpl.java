package com.wavemaker.todo.repository.impl;


import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UserEntity;
import com.wavemaker.todo.repository.UserEntityRepository;
import com.wavemaker.todo.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserEntityRepositoryImpl implements UserEntityRepository {
    private static final String SEARCH_USER = "SELECT * FROM TODO_USERS WHERE USER_NAME = ? AND USER_PASSWORD = ?";
    private static final  String GET_USER_BY_ID_SQL = "SELECT USER_ID, USER_NAME, USER_PASSWORD, EMAIL FROM TODO_USERS WHERE USER_ID = ?";
    private static Connection connection;

    public UserEntityRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    @Override
    public UserEntity authenticateUser(UserEntity userEntity) throws ServerUnavilableException {
        String username = userEntity.getUsername();
        String password = userEntity.getPassword();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_USER);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("USER_ID");
                String email = resultSet.getString("EMAIL");
                userEntity.setUserId(userId);
                userEntity.setEmail(email);

                return userEntity;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to Authenticate the User", 500);
        }
    }

    @Override
    public UserEntity getUserEntityById(int userId) throws ServerUnavilableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_ID_SQL);
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setUserId(resultSet.getInt("USER_ID"));
                    userEntity.setUsername(resultSet.getString("USER_NAME"));
                    userEntity.setPassword(resultSet.getString("USER_PASSWORD"));
                    userEntity.setEmail(resultSet.getString("EMAIL"));

                    return userEntity;
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Failed to retrieve user entity by ID.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return null;
    }


}
