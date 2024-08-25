package com.wavemaker.todo.controller;

import com.google.gson.Gson;
import com.wavemaker.todo.config.GsonConfig;
import com.wavemaker.todo.exception.ErrorResponse;
import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.Task;
import com.wavemaker.todo.pojo.UserEntity;
import com.wavemaker.todo.service.TaskService;
import com.wavemaker.todo.service.UserCookieService;
import com.wavemaker.todo.service.impl.TaskServiceImpl;
import com.wavemaker.todo.service.impl.UserCookieServiceImpl;
import com.wavemaker.todo.util.CookieHandler;
import jakarta.servlet.ServletConfig;
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
        UserEntity userEntity = null;
        List<Task> taskList = null;
        Task task = null;
        String taskId = httpServletRequest.getParameter("taskId");
        String searchTerm = httpServletRequest.getParameter("searchTerm");
        String priorityFilter = httpServletRequest.getParameter("priority");
        String sortBy = httpServletRequest.getParameter("sortBy");
        String sortOrder = httpServletRequest.getParameter("sortOrder");
        String cookieValue = CookieHandler.getCookieValueByCookieName("my_auth_cookie", httpServletRequest);
        if (cookieValue != null) {
            try {
                userEntity = userCookieService.getUserEntityByCookieValue(cookieValue);
                if (userEntity == null) {
                    ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                    jsonResponse = gson.toJson(errorResponse);
                    httpServletResponse.setStatus(400);
                    sendResponse(httpServletResponse, jsonResponse);
                }

                if (taskId != null) {
                    task = taskService.getTaskById(Integer.parseInt(taskId));
                    jsonResponse = gson.toJson(task);
                } else {
                    if (userEntity != null) {
                        taskList = taskService.getAllTasks(userEntity.getUserId(), searchTerm, priorityFilter, sortBy, sortOrder);
                        jsonResponse = gson.toJson(taskList);
                    }
                }
            } catch (ServerUnavilableException e) {
                logger.error("Error fetching Task details ", e);
                ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
                jsonResponse = gson.toJson(errorResponse);
            } catch (Exception e) {
                ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                httpServletResponse.setStatus(500);
                jsonResponse = gson.toJson(errorResponse);
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        String taskId = httpServletRequest.getParameter("taskId");
        String markAsCompleted = httpServletRequest.getParameter("markAsCompleted");
        Task task = null;
        int userId = -1;
        Task addedTask = null;
        BufferedReader bufferedReader = null;
        String cookieValue = CookieHandler.getCookieValueByCookieName("my_auth_cookie", httpServletRequest);
        if (cookieValue != null) {
            try {
                UserEntity userEntity = userCookieService.getUserEntityByCookieValue(cookieValue);
                if (userEntity == null) {
                    ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                    jsonResponse = gson.toJson(errorResponse);
                    httpServletResponse.setStatus(400);
                    sendResponse(httpServletResponse, jsonResponse);
                }

                if (taskId != null && markAsCompleted != null) {
                    task = taskService.markTaskAsCompleted(userId, Integer.parseInt(taskId), Boolean.parseBoolean(markAsCompleted));
                    jsonResponse = gson.toJson(task);
                } else {
                    bufferedReader = httpServletRequest.getReader();
                    task = gson.fromJson(bufferedReader, Task.class);
                    task.setUserId(userId);
                    addedTask = taskService.addTask(task);
                    jsonResponse = gson.toJson(addedTask);
                }
                httpServletResponse.setStatus(201);

            } catch (ServerUnavilableException | IOException e) {
                logger.error("Error fetching Task details ", e);
                ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
                jsonResponse = gson.toJson(errorResponse);
            } catch (Exception e) {
                ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                httpServletResponse.setStatus(500);
                jsonResponse = gson.toJson(errorResponse);
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
            }
        }
    }


    @Override
    protected void doPut(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        Task updatedTask = null;
        Task task = null;
        int taskId = Integer.parseInt(httpServletRequest.getParameter("taskId"));
        BufferedReader bufferedReader = null;
        String cookieValue = CookieHandler.getCookieValueByCookieName("my_auth_cookie", httpServletRequest);
        if (cookieValue != null) {
            try {
                UserEntity userEntity = userCookieService.getUserEntityByCookieValue(cookieValue);
                if (userEntity == null) {
                    ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                    jsonResponse = gson.toJson(errorResponse);
                    httpServletResponse.setStatus(400);
                    sendResponse(httpServletResponse, jsonResponse);
                }
                bufferedReader = httpServletRequest.getReader();
                task = gson.fromJson(bufferedReader, Task.class);
                task.setTaskId(taskId);
                updatedTask = taskService.updateTask(task);
                task = null;
                jsonResponse = gson.toJson(updatedTask);


            } catch (ServerUnavilableException | IOException e) {
                logger.error("Error fetching Task details ", e);
                ErrorResponse errorResponse = new ErrorResponse("Unable to Update Task", HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse = gson.toJson(errorResponse);
            } catch (Exception e) {
                ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                httpServletResponse.setStatus(500);
                jsonResponse = gson.toJson(errorResponse);
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        Task task = null;
        int taskId = Integer.parseInt(httpServletRequest.getParameter("taskId"));
        String cookieValue = CookieHandler.getCookieValueByCookieName("my_auth_cookie", httpServletRequest);
        if (cookieValue != null) {
            try {
                UserEntity userEntity = userCookieService.getUserEntityByCookieValue(cookieValue);
                if (userEntity == null) {
                    ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                    jsonResponse = gson.toJson(errorResponse);
                    httpServletResponse.setStatus(400);
                    sendResponse(httpServletResponse, jsonResponse);
                }
                task = taskService.deleteTaskById(taskId);
                jsonResponse = gson.toJson(task);
            } catch (ServerUnavilableException e) {
                logger.error("Error fetching Task details ", e);
                ErrorResponse errorResponse = new ErrorResponse("Unable to Delete Task", HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse = gson.toJson(errorResponse);
            } catch (Exception e) {
                ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                httpServletResponse.setStatus(500);
                jsonResponse = gson.toJson(errorResponse);
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
            }
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
