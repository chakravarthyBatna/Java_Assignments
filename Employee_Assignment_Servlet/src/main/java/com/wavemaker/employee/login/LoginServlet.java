package com.wavemaker.employee.login;

import com.google.gson.Gson;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.pojo.UserNamePassword;
import com.wavemaker.employee.service.UserService;
import com.wavemaker.employee.service.impl.UserServiceImpl;
import com.wavemaker.employee.util.CookieHolder;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private static UserService userService = null;
    private static Gson gson = null;

    @Override
    public void init(ServletConfig config) {
        try {
            gson = new Gson();
            userService = new UserServiceImpl();
        } catch (SQLException e) {
            logger.error("Exception : ",e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Login Request Received");
        ErrorResponse errorResponse = null;
        String jsonResponse = null;
        UserNamePassword userNamePassword = null;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = request.getReader();
            userNamePassword = gson.fromJson(bufferedReader, UserNamePassword.class);
            logger.info(" UserName : {}", userNamePassword.getUsername());
            if (userService.authenticateUser(userNamePassword)) {
                String cookieValue = UUID.randomUUID().toString(); //generating random cookie
                String cookieName = "my_auth_cookie";  //set default cookie value
                Cookie cookie = new Cookie(cookieName, cookieValue); //creating a cookie with name,value;
                CookieHolder.addUser(cookieValue, userNamePassword);

                logger.info("random cookie generated for new user : {}", cookieValue);
                HttpSession userSession = request.getSession(true); //userSession created
                userSession.setAttribute(cookieValue, userNamePassword); //added user to userSession
                logger.info("User Session is set to new user");
                logger.info("user cookie added successfully username : {} and cookie : {}", userNamePassword.getUsername(), cookieValue);
                response.addCookie(cookie); //added cookie to response

                logger.info("User Cookie added Successfully to browser : {}", cookie);
                jsonResponse = "User Found to be admin";
            } else {
                errorResponse = new ErrorResponse("Invalid UserName and Password", 401);
                jsonResponse = gson.toJson(errorResponse);
                logger.error("Invalid User Found : Username : {} and User Password : {}", userNamePassword.getUsername(), userNamePassword);
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
