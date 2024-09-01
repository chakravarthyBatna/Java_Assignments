package com.wavemaker.employee.repository;


import com.wavemaker.employee.exception.ServerUnavilableException;

public interface UserCookieRepository {
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavilableException;

    public int getUserIdByCookieValue(String cookieValue) throws ServerUnavilableException;

    public boolean deleteUserCookie(String cookieValue) throws ServerUnavilableException;

}
