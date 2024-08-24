package com.wavemaker.employee.service;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserNamePassword;

public interface UserService {
    public boolean authenticateUser(UserNamePassword userNamePassword) throws ServerUnavilableException;
}
