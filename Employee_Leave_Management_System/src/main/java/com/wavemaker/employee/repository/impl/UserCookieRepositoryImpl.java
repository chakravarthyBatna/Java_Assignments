package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.repository.UserCookieRepository;
import com.wavemaker.employee.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCookieRepositoryImpl implements UserCookieRepository {
    private static final String GET_EMPLOYEE_ID = "SELECT EMP_ID FROM EMPLOYEE_COOKIES WHERE COOKIE_VALUE = ?";

    private static final String INSERT_COOKIE_SQL = "INSERT INTO EMPLOYEE_COOKIES (COOKIE_NAME, COOKIE_VALUE, EMP_ID) VALUES (?, ?, ?)";

    private static final String DELETE_USER_COOKIE = "DELETE FROM EMPLOYEE_COOKIES WHERE COOKIE_VALUE = ?";

    private static Connection connection;

    public UserCookieRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    @Override
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavilableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COOKIE_SQL);
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
            PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_ID);
            preparedStatement.setString(1, cookieValue);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("EMP_ID");
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Failed to retrieve user ID by cookie value.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return -1;
    }

    @Override
    public boolean deleteUserCookie(String cookieValue) throws ServerUnavilableException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_COOKIE)) {
            preparedStatement.setString(1, cookieValue);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new ServerUnavilableException("Failed to delete user cookie.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
