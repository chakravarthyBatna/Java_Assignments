package com.wavemaker.employee.util;


import com.mysql.cj.jdbc.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private static final Logger logger = LoggerFactory.getLogger(DBConnector.class);
    //constants
    private final static String DB_URL = "jdbc:mysql://127.0.0.1:3306/employee_leave_management";
    private final static String DB_USERNAME = "root";
    private final static String DB_PASSWORD = "root123";
    private static volatile Connection connection;

    private DBConnector() {
    }

    public static Connection getConnectionInstance() throws SQLException {
        try {
            if (connection == null || !connection.isClosed()) {
                synchronized (DBConnector.class) {
                    if (connection == null || !connection.isClosed()) {
                        DriverManager.registerDriver(new Driver());
                        connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    }
                }
            }
        } catch (SQLException e) {
            logger.warn("Exception :", e);
        }
        return connection;
    }
}
