package com.wavemaker.employee.repository.impl.indb;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserNamePassword;
import com.wavemaker.employee.repository.UserPasswordRepository;
import com.wavemaker.employee.util.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPasswordRepositoryImpl implements UserPasswordRepository {
    String SEARCH_USER = "SELECT * FROM USERS WHERE USERNAME = ? AND USERPASSWORD = ?";
    private static Connection connection;

    public UserPasswordRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    @Override
    public boolean authenticateUser(UserNamePassword userNamePassword) throws ServerUnavilableException {
        String username = userNamePassword.getUsername();
        String password = userNamePassword.getPassword();
        boolean userExists = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_USER);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userExists = true;
            }

        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to Authenticate the User", 500);
        }
        return userExists;
    }

}
