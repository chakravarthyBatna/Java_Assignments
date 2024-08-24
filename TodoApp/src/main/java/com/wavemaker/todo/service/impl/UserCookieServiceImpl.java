package com.wavemaker.todo.service.impl;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.repository.UserCookieRepository;
import com.wavemaker.todo.repository.impl.UserCookieRepositoryImpl;
import com.wavemaker.todo.service.UserCookieService;

import java.sql.SQLException;

public class UserCookieServiceImpl implements UserCookieService {
    private static UserCookieRepository userCookieRepository;

    public UserCookieServiceImpl() throws SQLException {
        userCookieRepository = new UserCookieRepositoryImpl();
    }

    @Override
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavilableException {
        return userCookieRepository.addCookie(cookieValue, userId);
    }

    @Override
    public int getUserIdByCookieValue(String cookieValue) throws ServerUnavilableException {
        return userCookieRepository.getUserIdByCookieValue(cookieValue);
    }
}
