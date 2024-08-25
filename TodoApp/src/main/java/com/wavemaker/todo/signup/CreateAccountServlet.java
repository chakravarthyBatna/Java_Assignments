package com.wavemaker.todo.signup;

import com.google.gson.Gson;
import com.google.protobuf.ServiceException;
import com.wavemaker.todo.config.GsonConfig;
import com.wavemaker.todo.exception.ErrorResponse;
import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.UserEntity;
import com.wavemaker.todo.service.UserEntityService;
import com.wavemaker.todo.service.impl.UserEntityServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
@WebServlet("/signup")
public class CreateAccountServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CreateAccountServlet.class);
    private Gson gson = null;
    private UserEntityService userEntityService = null;

    @Override
    public void init(ServletConfig config) {
        gson = GsonConfig.createGson();
        try {
            userEntityService = new UserEntityServiceImpl();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String userName = httpServletRequest.getParameter("userName");
        String userPassword = httpServletRequest.getParameter("userPassword");
        String email = httpServletRequest.getParameter("email");
        String jsonResponse = null;
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userName);
        userEntity.setPassword(userPassword);
        userEntity.setEmail(email);
        try {
            UserEntity addUserEntity = userEntityService.addUserEntity(userEntity);
            if (addUserEntity == null) {
                ErrorResponse errorResponse = new ErrorResponse("Error While Adding the User", HttpServletResponse.SC_CONFLICT);
                jsonResponse = gson.toJson(errorResponse);
                sendResponse(httpServletResponse, jsonResponse);
            }
            httpServletResponse.setStatus(201);
            httpServletResponse.sendRedirect("Login.html");
        } catch (ServerUnavilableException | IOException e) {
            ErrorResponse errorResponse = new ErrorResponse("Could not add user",HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse = gson.toJson(errorResponse);
        } finally {
            sendResponse(httpServletResponse, jsonResponse);
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
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpServletResponse.setStatus(500);
            jsonResponse = gson.toJson(errorResponse);
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
