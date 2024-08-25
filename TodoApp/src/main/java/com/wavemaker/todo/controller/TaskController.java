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
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    private TaskService taskService = null;
    private UserCookieService userCookieService = null;
    private Gson gson = null;

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
        HttpSession session = null;
        String taskId = httpServletRequest.getParameter("taskId");
        String searchTerm = httpServletRequest.getParameter("searchTerm");
        String priorityFilter = httpServletRequest.getParameter("priority");
        String sortBy = httpServletRequest.getParameter("sortBy");
        String sortOrder = httpServletRequest.getParameter("sortOrder");

        logger.info("Received GET request with parameters: taskId={}, searchTerm={}, priority={}, sortBy={}, sortOrder={}",
                taskId, searchTerm, priorityFilter, sortBy, sortOrder);

        try {
            session = httpServletRequest.getSession(true);
            logger.info("Session retrieved: sessionId={}", session.getId());

            userEntity = (UserEntity) session.getAttribute("my_user");
            if (userEntity == null) {
                logger.warn("Invalid user found in session: sessionId={}", session.getId());
                ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(400);
                sendResponse(httpServletResponse, jsonResponse);
                return;
            }

            if (taskId != null) {
                logger.info("Fetching task with ID: {}", taskId);
                task = taskService.getTaskById(Integer.parseInt(taskId));
                jsonResponse = gson.toJson(task);
            } else {
                logger.info("Fetching all tasks for user ID: {}", userEntity.getUserId());
                taskList = taskService.getAllTasks(userEntity.getUserId(), searchTerm, priorityFilter, sortBy, sortOrder);
                jsonResponse = gson.toJson(taskList);
            }
        } catch (ServerUnavilableException e) {
            logger.error("Error fetching Task details for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown", e);
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
            jsonResponse = gson.toJson(errorResponse);
        } catch (Exception e) {
            logger.error("Server error occurred while processing GET request", e);
            ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpServletResponse.setStatus(500);
            jsonResponse = gson.toJson(errorResponse);
        } finally {
            sendResponse(httpServletResponse, jsonResponse);
            logger.info("Response sent with status: {}", httpServletResponse.getStatus());
        }
    }


    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        String taskId = httpServletRequest.getParameter("taskId");
        String markAsCompleted = httpServletRequest.getParameter("markAsCompleted");
        Task task = null;
        HttpSession session = null;
        UserEntity userEntity = null;
        int userId = -1;
        Task addedTask = null;
        BufferedReader bufferedReader = null;

        logger.info("Received POST request with parameters: taskId={}, markAsCompleted={}", taskId, markAsCompleted);

        try {
            session = httpServletRequest.getSession(true);
            logger.info("Session retrieved: sessionId={}", session.getId());

            userEntity = (UserEntity) session.getAttribute("my_user");
            if (userEntity == null) {
                logger.warn("Invalid user found in session: sessionId={}", session.getId());
                ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(400);
                sendResponse(httpServletResponse, jsonResponse);
                return;
            }

            userId = userEntity.getUserId();
            logger.info("Processing request for userId={}", userId);

            if (taskId != null && markAsCompleted != null) {
                logger.info("Marking task as completed: taskId={}, markAsCompleted={}", taskId, markAsCompleted);
                task = taskService.markTaskAsCompleted(userId, Integer.parseInt(taskId), Boolean.parseBoolean(markAsCompleted));
                jsonResponse = gson.toJson(task);
            } else {
                logger.info("Adding new task for userId={}", userId);
                bufferedReader = httpServletRequest.getReader();
                task = gson.fromJson(bufferedReader, Task.class);
                task.setUserId(userId);
                addedTask = taskService.addTask(task);
                jsonResponse = gson.toJson(addedTask);
            }

            httpServletResponse.setStatus(201);
            logger.info("POST request processed successfully with status 201");

        } catch (ServerUnavilableException | IOException e) {
            logger.error("Error processing POST request for userId={}", userId, e);
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
            jsonResponse = gson.toJson(errorResponse);
        } catch (Exception e) {
            logger.error("Server error occurred while processing POST request for userId={}", userId, e);
            ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpServletResponse.setStatus(500);
            jsonResponse = gson.toJson(errorResponse);
        } finally {
            sendResponse(httpServletResponse, jsonResponse);
            logger.info("Response sent with status: {}", httpServletResponse.getStatus());
        }
    }


    @Override
    protected void doPut(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        HttpSession session = null;
        UserEntity userEntity = null;
        Task updatedTask = null;
        Task task = null;
        int taskId = Integer.parseInt(httpServletRequest.getParameter("taskId"));
        BufferedReader bufferedReader = null;

        logger.info("Received PUT request to update task with taskId={}", taskId);

        try {
            session = httpServletRequest.getSession(true);
            logger.info("Session retrieved: sessionId={}", session.getId());

            userEntity = (UserEntity) session.getAttribute("my_user");
            if (userEntity == null) {
                logger.warn("Invalid user found in session: sessionId={}", session.getId());
                ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(400);
                sendResponse(httpServletResponse, jsonResponse);
                return;
            }

            logger.info("Reading task data from request body for userId={}", userEntity.getUserId());
            bufferedReader = httpServletRequest.getReader();
            task = gson.fromJson(bufferedReader, Task.class);
            task.setTaskId(taskId);

            logger.info("Updating task with taskId={} for userId={}", taskId, userEntity.getUserId());
            updatedTask = taskService.updateTask(userEntity.getUserId(), task);
            task = null;
            jsonResponse = gson.toJson(updatedTask);
            logger.info("Task updated successfully with taskId={}", taskId);

        } catch (ServerUnavilableException | IOException e) {
            logger.error("Error updating task with taskId={} for userId={}", taskId, userEntity != null ? userEntity.getUserId() : "unknown", e);
            ErrorResponse errorResponse = new ErrorResponse("Unable to Update Task", HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse = gson.toJson(errorResponse);
        } catch (Exception e) {
            logger.error("Server error occurred while updating task with taskId={} for userId={}", taskId, userEntity != null ? userEntity.getUserId() : "unknown", e);
            ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpServletResponse.setStatus(500);
            jsonResponse = gson.toJson(errorResponse);
        } finally {
            sendResponse(httpServletResponse, jsonResponse);
            logger.info("Response sent for taskId={} with status: {}", taskId, httpServletResponse.getStatus());
        }
    }


    @Override
    protected void doDelete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        HttpSession session = null;
        UserEntity userEntity = null;
        Task task = null;
        int taskId = Integer.parseInt(httpServletRequest.getParameter("taskId"));

        logger.info("Received DELETE request to delete task with taskId={}", taskId);

        try {
            session = httpServletRequest.getSession(true);
            logger.info("Session retrieved: sessionId={}", session.getId());

            userEntity = (UserEntity) session.getAttribute("my_user");
            if (userEntity == null) {
                logger.warn("Invalid user found in session: sessionId={}", session.getId());
                ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(400);
                sendResponse(httpServletResponse, jsonResponse);
                return;
            }

            logger.info("Attempting to delete task with taskId={} for userId={}", taskId, userEntity.getUserId());
            task = taskService.deleteTaskById(userEntity.getUserId(), taskId);
            jsonResponse = gson.toJson(task);
            logger.info("Task with taskId={} deleted successfully for userId={}", taskId, userEntity.getUserId());

        } catch (ServerUnavilableException e) {
            logger.error("Error deleting task with taskId={} for userId={}", taskId, userEntity != null ? userEntity.getUserId() : "unknown", e);
            ErrorResponse errorResponse = new ErrorResponse("Unable to Delete Task", HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse = gson.toJson(errorResponse);
        } catch (Exception e) {
            logger.error("Server error occurred while deleting task with taskId={} for userId={}", taskId, userEntity != null ? userEntity.getUserId() : "unknown", e);
            ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpServletResponse.setStatus(500);
            jsonResponse = gson.toJson(errorResponse);
        } finally {
            sendResponse(httpServletResponse, jsonResponse);
            logger.info("Response sent for DELETE request on taskId={} with status: {}", taskId, httpServletResponse.getStatus());
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
