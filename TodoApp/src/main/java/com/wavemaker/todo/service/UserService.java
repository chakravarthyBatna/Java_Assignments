package com.wavemaker.todo.service;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UsernameAndPassword;

public interface UserService {
    public int authenticateUser(UsernameAndPassword usernameAndPassword) throws ServerUnavilableException;
}
