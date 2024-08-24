package com.wavemaker.employee.util;

import com.wavemaker.employee.pojo.UserNamePassword;

import java.util.HashMap;
import java.util.Map;

public class CookieHolder {
    private static final Map<String, UserNamePassword> userCookies = new HashMap<>();

    public static void addUser(String cookieValue, UserNamePassword userNamePassword) {
        userCookies.put(cookieValue, userNamePassword);
    }

    public static UserNamePassword getUserByCookieName(String cookieValue) {
        return userCookies.get(cookieValue);
    }

    public static UserNamePassword removeUser(String cookieValue) {
        return userCookies.remove(cookieValue);
    }
}
