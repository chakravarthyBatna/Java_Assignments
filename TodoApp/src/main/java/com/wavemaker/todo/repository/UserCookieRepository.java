package com.wavemaker.todo.repository;

import com.wavemaker.todo.exception.ServerUnavilableException;

public interface UserCookieRepository {
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavilableException;
    public int getUserIdByCookieValue(String cookieValue) throws ServerUnavilableException;
}
