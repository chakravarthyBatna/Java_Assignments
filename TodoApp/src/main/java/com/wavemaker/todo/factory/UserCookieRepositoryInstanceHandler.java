package com.wavemaker.todo.factory;

import com.wavemaker.todo.repository.UserCookieRepository;
import com.wavemaker.todo.repository.impl.UserCookieRepositoryImpl;

import java.sql.SQLException;

public class UserCookieRepositoryInstanceHandler {
    private static volatile UserCookieRepository userCookieRepository;
    public static UserCookieRepository getUserCookieRepositoryInstance() throws SQLException {
        if (userCookieRepository == null) {
            synchronized (UserCookieRepositoryInstanceHandler.class) {
                if (userCookieRepository == null) {
                    userCookieRepository = new UserCookieRepositoryImpl();
                }
            }
        }
        return userCookieRepository;
    }
}
