package com.wavemaker.employee.util;

import com.google.gson.Gson;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.pojo.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;

public class UserSessionHandler {
    public static UserEntity handleUserSessionAndReturnUserEntity(HttpServletRequest request, HttpServletResponse response, Logger logger) {
        Gson gson = new Gson();
        String jsonResponse = null;
        HttpSession session = null;
        UserEntity userEntity = null;
        session = request.getSession(true);
        logger.info("Session retrieved: sessionId={}", session.getId());
        userEntity = (UserEntity) session.getAttribute("my_user");
        if (userEntity == null) {
            logger.warn("Invalid user found in session: sessionId={}", session.getId());
            ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse = gson.toJson(errorResponse);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ClientResponseHandler.sendResponseToClient(response, jsonResponse, logger);
        }
        return userEntity;
    }
}
