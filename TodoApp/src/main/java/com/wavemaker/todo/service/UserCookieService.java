package com.wavemaker.todo.service;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UserEntity;

public interface UserCookieService {
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavilableException;

    public UserEntity getUserEntityByCookieValue(String cookieValue) throws ServerUnavilableException;
}
