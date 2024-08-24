package com.wavemaker.todo.service.impl;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UsernameAndPassword;
import com.wavemaker.todo.repository.UserPasswordRepository;
import com.wavemaker.todo.repository.impl.UserPasswordRepositoryImpl;
import com.wavemaker.todo.service.UserService;

import java.sql.SQLException;

public class UserServiceImpl implements UserService {
    private static UserPasswordRepository userPasswordRepository = null;

    public UserServiceImpl() throws SQLException {
        userPasswordRepository = new UserPasswordRepositoryImpl();
    }

    @Override
    public int authenticateUser(UsernameAndPassword usernameAndPassword) throws ServerUnavilableException {
        return userPasswordRepository.authenticateUser(usernameAndPassword);
    }

}
