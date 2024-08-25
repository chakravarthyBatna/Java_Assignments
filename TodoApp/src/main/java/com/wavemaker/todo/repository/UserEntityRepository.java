package com.wavemaker.todo.repository;


import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UserEntity;

public interface UserEntityRepository {
    public UserEntity authenticateUser(UserEntity userEntity) throws ServerUnavilableException;

    public UserEntity getUserEntityById(int userId) throws ServerUnavilableException;
}
