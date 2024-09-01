package com.wavemaker.employee.factory;


import com.wavemaker.employee.repository.UserCookieRepository;
import com.wavemaker.employee.repository.impl.UserCookieRepositoryImpl;

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
