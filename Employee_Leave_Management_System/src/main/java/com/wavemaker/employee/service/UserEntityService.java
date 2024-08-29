package com.wavemaker.employee.service;


import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserEntity;

public interface UserEntityService {
    public UserEntity authenticateUser(UserEntity userEntity) throws ServerUnavilableException;

    public UserEntity getUserEntityById(int userId) throws ServerUnavilableException;

    public UserEntity addUserEntity(UserEntity userEntity) throws ServerUnavilableException;
}
