package com.wavemaker.todo.repository.impl;


import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UsernameAndPassword;
import com.wavemaker.todo.repository.UserPasswordRepository;
import com.wavemaker.todo.util.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPasswordRepositoryImpl implements UserPasswordRepository {
    String SEARCH_USER = "SELECT * FROM TODO_USERS WHERE USER_NAME = ? AND USER_PASSWORD = ?";
    private static Connection connection;

    public UserPasswordRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    @Override
    public int authenticateUser(UsernameAndPassword usernameAndPassword) throws ServerUnavilableException {
        String username = usernameAndPassword.getUsername();
        String password = usernameAndPassword.getPassword();
        int userId = -1;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_USER);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt("USER_ID");
            }

        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to Authenticate the User", 500);
        }
        return userId;
    }

}
