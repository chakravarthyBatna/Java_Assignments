package com.wavemaker.todo.repository.impl;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.repository.UserCookieRepository;
import com.wavemaker.todo.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCookieRepositoryImpl implements UserCookieRepository {
    private static Connection connection;
    private static final String GET_USER_ID = "SELECT USER_ID FROM USER_COOKIES WHERE COOKIE_VALUE = ?";

    public UserCookieRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    @Override
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavilableException {
        String insertCookieSQL = "INSERT INTO USER_COOKIES (COOKIE_NAME, COOKIE_VALUE, USER_ID) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertCookieSQL);
            String cookieName = "my_auth_cookie"; //default cookie name
            preparedStatement.setString(1, cookieName);
            preparedStatement.setString(2, cookieValue);
            preparedStatement.setInt(3, userId);

            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw new ServerUnavilableException("Failed to add cookie due to a database error.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public int getUserIdByCookieValue(String cookieValue) throws ServerUnavilableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_ID);
            preparedStatement.setString(1, cookieValue);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("USER_ID");
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Failed to retrieve user ID by cookie value.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return -1;
    }

}
