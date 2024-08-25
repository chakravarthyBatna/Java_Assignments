package com.wavemaker.todo.login;

import com.google.gson.Gson;
import com.wavemaker.todo.exception.ErrorResponse;
import com.wavemaker.todo.pojo.UserEntity;
import com.wavemaker.todo.service.UserCookieService;
import com.wavemaker.todo.service.UserEntityService;
import com.wavemaker.todo.service.impl.UserCookieServiceImpl;
import com.wavemaker.todo.service.impl.UserEntityServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.UUID;

public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private static UserEntityService userEntityService = null;
    private static Gson gson = null;
    private static UserCookieService userCookieService = null;

    @Override
    public void init(ServletConfig config) {
        try {
            gson = new Gson();
            userEntityService = new UserEntityServiceImpl();
            userCookieService = new UserCookieServiceImpl();
        } catch (SQLException e) {
            logger.error("Exception : ", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Login Request Received");
        ErrorResponse errorResponse = null;
        String jsonResponse = null;
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(password);
        try {
            logger.info(" UserName : {}", userEntity.getUsername());
            UserEntity authenticatedUser = userEntityService.authenticateUser(userEntity);
            if (authenticatedUser != null) {
                String cookieValue = UUID.randomUUID().toString(); //generating random cookie
                String cookieName = "my_auth_cookie";  //set default cookie value
                Cookie cookie = new Cookie(cookieName, cookieValue); //creating a cookie with name,value;

                userCookieService.addCookie(cookieValue, authenticatedUser.getUserId());

                logger.info("user cookie added successfully username : {} and cookie : {}", authenticatedUser.getUsername(), cookieValue);
                response.addCookie(cookie); //added cookie to response

                logger.info("User Cookie added Successfully to browser : {}", cookie);
                response.sendRedirect("index.html");
            } else {
                errorResponse = new ErrorResponse("Invalid UserName and Password", 401);
                jsonResponse = gson.toJson(errorResponse);
                logger.error("Invalid User Found : Username : {} and User Password : {}", authenticatedUser.getUsername(), authenticatedUser);
            }
        } catch (Exception e) {
            errorResponse = new ErrorResponse("Error - While logging : " + e.getMessage(), 500);
            jsonResponse = gson.toJson(errorResponse);
            logger.error("Error Occurred while trying to Login ", e);
        } finally {
            sendResponse(response, jsonResponse);
        }

    }

    private void sendResponse(HttpServletResponse httpServletResponse, String jsonResponse) {
        PrintWriter printWriter = null;
        try {
            logger.info("Preparing response to send back to client");
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            printWriter = httpServletResponse.getWriter();
            printWriter.print(jsonResponse);
            printWriter.flush();
            logger.info("Response successfully sent back to client");
        } catch (IOException e) {
            logger.error("Error writing response back to client", e);
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", 500);
            jsonResponse = gson.toJson(errorResponse);
            httpServletResponse.setStatus(500);
            printWriter.print(jsonResponse);
            printWriter.flush();
        } finally {
            closePrintWriter(printWriter);
        }
    }

    private void closePrintWriter(PrintWriter printWriter) {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}
