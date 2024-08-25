package com.wavemaker.todo.service.impl;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UserEntity;
import com.wavemaker.todo.repository.UserCookieRepository;
import com.wavemaker.todo.repository.impl.UserCookieRepositoryImpl;
import com.wavemaker.todo.service.UserCookieService;
import com.wavemaker.todo.service.UserEntityService;

import java.sql.SQLException;

public class UserCookieServiceImpl implements UserCookieService {
    private static UserCookieRepository userCookieRepository;
    private static UserEntityService userEntityService;

    public UserCookieServiceImpl() throws SQLException {
        userCookieRepository = new UserCookieRepositoryImpl();
        userEntityService = new UserEntityServiceImpl();
    }

    @Override
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavilableException {
        return userCookieRepository.addCookie(cookieValue, userId);
    }

    @Override
    public UserEntity getUserEntityByCookieValue(String cookieValue) throws ServerUnavilableException {
        int userId =  userCookieRepository.getUserIdByCookieValue(cookieValue);
        if (userId != -1) {
            return userEntityService.getUserEntityById(userId);
        }
        return null;
    }
}
