package com.wavemaker.todo.controller;

import com.google.gson.Gson;
import com.wavemaker.todo.config.GsonConfig;
import com.wavemaker.todo.exception.ErrorResponse;
import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.Task;
import com.wavemaker.todo.service.TaskService;
import com.wavemaker.todo.service.UserCookieService;
import com.wavemaker.todo.service.impl.TaskServiceImpl;
import com.wavemaker.todo.service.impl.UserCookieServiceImpl;
import com.wavemaker.todo.util.CookieHandler;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/tasks")
public class TaskController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private static TaskService taskService = null;
    private static UserCookieService userCookieService = null;
    private static Gson gson = null;

    public TaskController() {
    }

    @Override
    public void init(ServletConfig config) {
        try {
            taskService = new TaskServiceImpl();
            userCookieService = new UserCookieServiceImpl();
            gson = GsonConfig.createGson();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        List<Task> taskList = null;
        String cookieValue = CookieHandler.getCookieValueByCookieName("my_auth_cookie", httpServletRequest);
        if (cookieValue != null) {
            try {
                int userId = userCookieService.getUserIdByCookieValue(cookieValue);
                if (userId != -1) {
                    taskList = taskService.getAllTasks(userId);
                    jsonResponse = gson.toJson(taskList);
                } else {
                    ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                    jsonResponse = gson.toJson(errorResponse);
                    httpServletResponse.setStatus(400);
                    sendResponse(httpServletResponse, jsonResponse);
                }
            } catch (ServerUnavilableException e) {
                logger.error("Error fetching Task details ", e);
                ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
                jsonResponse = gson.toJson(errorResponse);
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String jsonResponse = null;
        Task task = null;
        int userId = -1;
        Task addedTask = null;
        BufferedReader bufferedReader = null;
        String cookieValue = CookieHandler.getCookieValueByCookieName("my_auth_cookie", httpServletRequest);
        if (cookieValue != null) {
            try {
                userId = userCookieService.getUserIdByCookieValue(cookieValue);
                if (userId != -1) {
                    bufferedReader = httpServletRequest.getReader();
                    task = gson.fromJson(bufferedReader, Task.class);
                    task.setUserId(userId);
                    addedTask = taskService.addTask(task);
                    jsonResponse = gson.toJson(addedTask);
                } else {
                    ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                    jsonResponse = gson.toJson(errorResponse);
                    httpServletResponse.setStatus(400);
                    sendResponse(httpServletResponse, jsonResponse);
                }
            } catch (ServerUnavilableException e) {
                logger.error("Error fetching Task details ", e);
                ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
                jsonResponse = gson.toJson(errorResponse);
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
            }
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
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
