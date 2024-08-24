package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserNamePassword;
import com.wavemaker.employee.repository.UserPasswordRepository;
import com.wavemaker.employee.repository.impl.indb.UserPasswordRepositoryImpl;
import com.wavemaker.employee.service.UserService;

import java.sql.SQLException;

public class UserServiceImpl implements UserService {
    private static UserPasswordRepository userPasswordRepository = null;

    public UserServiceImpl() throws SQLException {
        userPasswordRepository = new UserPasswordRepositoryImpl();
    }

    @Override
    public boolean authenticateUser(UserNamePassword userNamePassword) throws ServerUnavilableException {
        return userPasswordRepository.authenticateUser(userNamePassword);
    }
}
