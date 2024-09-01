package com.wavemaker.employee.service;


import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserEntity;

public interface UserCookieService {
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavilableException;

    public UserEntity getUserEntityByCookieValue(String cookieValue) throws ServerUnavilableException;

    public boolean deleteUserCookie(String cookieValue) throws ServerUnavilableException;
}
