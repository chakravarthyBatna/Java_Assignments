package com.wavemaker.todo.service;

import com.wavemaker.todo.exception.ServerUnavilableException;

public interface UserCookieService {
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavilableException;

    public int getUserIdByCookieValue(String cookieValue) throws ServerUnavilableException;
}
