package com.wavemaker.employee.factory;

import com.wavemaker.employee.repository.UserEntityRepository;
import com.wavemaker.employee.repository.impl.UserEntityRepositoryImpl;

import java.sql.SQLException;

public class UserEntityRepositoryInstanceHandler {
    private static volatile UserEntityRepository userEntityRepository;

    public static UserEntityRepository getUserEntityRepositoryInstance() throws SQLException {
        if (userEntityRepository == null) {
            synchronized (UserEntityRepositoryInstanceHandler.class) {
                if (userEntityRepository == null) {
                    userEntityRepository = new UserEntityRepositoryImpl();
                }
            }
        }
        return userEntityRepository;
    }
}
