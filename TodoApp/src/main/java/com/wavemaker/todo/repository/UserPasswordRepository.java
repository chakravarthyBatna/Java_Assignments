package com.wavemaker.todo.repository;


import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UsernameAndPassword;

public interface UserPasswordRepository {
    public int authenticateUser(UsernameAndPassword usernameAndPassword) throws ServerUnavilableException;
}
