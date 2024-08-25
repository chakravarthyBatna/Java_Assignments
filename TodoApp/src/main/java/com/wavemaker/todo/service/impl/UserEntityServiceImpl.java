package com.wavemaker.todo.service.impl;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UserEntity;
import com.wavemaker.todo.repository.UserEntityRepository;
import com.wavemaker.todo.repository.impl.UserEntityRepositoryImpl;
import com.wavemaker.todo.service.UserEntityService;

import java.sql.SQLException;

public class UserEntityServiceImpl implements UserEntityService {
    private static UserEntityRepository userEntityRepository = null;

    public UserEntityServiceImpl() throws SQLException {
        userEntityRepository = new UserEntityRepositoryImpl();
    }

    @Override
    public UserEntity authenticateUser(UserEntity userEntity) throws ServerUnavilableException {
        return userEntityRepository.authenticateUser(userEntity);
    }

    @Override
    public UserEntity getUserEntityById(int userId) throws ServerUnavilableException {
        return userEntityRepository.getUserEntityById(userId);
    }


}
