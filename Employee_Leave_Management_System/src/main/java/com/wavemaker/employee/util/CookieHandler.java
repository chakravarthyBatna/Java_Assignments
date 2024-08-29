package com.wavemaker.employee.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieHandler {
    public static String getCookieValueByCookieName(String cookieName, HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static Cookie invalidateCookie(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    cookie.setMaxAge(0);
                    cookie.setValue(null);
                    cookie.setPath(request.getContextPath());
                    cookie.setMaxAge(0);
                    return cookie;
                }
            }
        }
        return null;
    }
}
