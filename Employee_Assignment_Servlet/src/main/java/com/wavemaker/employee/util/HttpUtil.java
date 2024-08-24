package com.wavemaker.employee.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String getCookieByName(String cookieName, HttpServletRequest request) {
        Cookie[] cookiesFromRequest = request.getCookies();
        if (cookiesFromRequest != null) {
            for (Cookie cookie : cookiesFromRequest) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
