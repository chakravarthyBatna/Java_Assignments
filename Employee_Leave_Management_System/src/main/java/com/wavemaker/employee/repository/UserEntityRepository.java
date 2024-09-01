package com.wavemaker.employee.repository;


import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserEntity;

public interface UserEntityRepository {
    public UserEntity authenticateUser(UserEntity userEntity) throws ServerUnavilableException;

    public UserEntity getUserEntityById(int userId) throws ServerUnavilableException;

    public UserEntity addUserEntity(UserEntity userEntity) throws ServerUnavilableException;

}
