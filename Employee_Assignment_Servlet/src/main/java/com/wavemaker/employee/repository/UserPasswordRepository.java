package com.wavemaker.employee.repository;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserNamePassword;

public interface UserPasswordRepository {
    public boolean authenticateUser(UserNamePassword userNamePassword) throws ServerUnavilableException;
}
