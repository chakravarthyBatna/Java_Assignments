package com.wavemaker.todo.service;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UserEntity;

public interface UserEntityService {
    public UserEntity authenticateUser(UserEntity userEntity) throws ServerUnavilableException;

    public UserEntity getUserEntityById(int userId) throws ServerUnavilableException;

    public UserEntity addUserEntity(UserEntity userEntity) throws ServerUnavilableException;
}
