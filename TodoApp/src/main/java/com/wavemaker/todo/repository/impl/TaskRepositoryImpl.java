package com.wavemaker.todo.repository.impl;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.exception.TaskNotFoundException;
import com.wavemaker.todo.pojo.Task;
import com.wavemaker.todo.repository.TaskRepository;
import com.wavemaker.todo.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepositoryImpl implements TaskRepository {

    private static Connection connection = null;

    private static final String INSERT_QUERY = "INSERT INTO TASKS (USER_ID, TASK_NAME, DUE_DATE," +
            " DUE_TIME, PRIORITY, COMPLETED) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String GET_QUERY = "SELECT TASK_ID, USER_ID, TASK_NAME, DUE_DATE," +
            " DUE_TIME, PRIORITY, COMPLETED FROM TASKS WHERE TASK_ID = ?";


    private static final String UPDATE_QUERY = "UPDATE TASKS SET TASK_NAME = ?, " +
            "DUE_DATE = ?, DUE_TIME = ?, PRIORITY = ?, COMPLETED = ? WHERE TASK_ID = ?";

    private static final String DELETE_QUERY = "DELETE FROM TASKS WHERE TASK_ID = ?";


    public TaskRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }


    @Override
    public Task markTaskAsCompleted(int userId, int taskId, boolean markAsComplete) throws ServerUnavilableException, TaskNotFoundException {
        Task task = null;
        String query = "UPDATE TASKS SET COMPLETED = ? WHERE USER_ID = ? AND TASK_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, markAsComplete);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, taskId);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new TaskNotFoundException("Task not found for the given userId and taskId.", HttpServletResponse.SC_NOT_FOUND);
            }

            task = getTaskById(taskId);

        } catch (SQLException e) {
            throw new ServerUnavilableException("Error updating task completion status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return task;
    }


    @Override
    public Task addTask(Task task) throws ServerUnavilableException {
        int taskPriority = convertPriorityToNumber(task.getPriority());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, task.getUserId());
            preparedStatement.setString(2, task.getTaskName());
            preparedStatement.setDate(3, Date.valueOf(task.getDueDate()));
            preparedStatement.setTime(4, Time.valueOf(task.getDueTime()));
            preparedStatement.setInt(5, taskPriority);
            preparedStatement.setBoolean(6, task.isCompleted());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedTaskId = generatedKeys.getInt(1);
                        task.setTaskId(generatedTaskId);
                        return task;
                    } else {
                        throw new ServerUnavilableException("Creating task failed, no ID obtained.", 500);
                    }
                }
            } else {
                throw new ServerUnavilableException("Creating task failed, no rows affected.", 500);
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error adding task: " + e.getMessage(), 500);
        }
    }

    @Override
    public Task getTaskById(int taskId) throws ServerUnavilableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_QUERY);
            preparedStatement.setInt(1, taskId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Task task = new Task();
                    int taskPriority = resultSet.getInt("PRIORITY");
                    task.setTaskId(resultSet.getInt("TASK_ID"));
                    task.setUserId(resultSet.getInt("USER_ID"));
                    task.setTaskName(resultSet.getString("TASK_NAME"));
                    task.setDueDate(resultSet.getDate("DUE_DATE").toLocalDate());
                    task.setDueTime(resultSet.getTime("DUE_TIME").toLocalTime());
                    task.setCompleted(resultSet.getBoolean("COMPLETED"));
                    task.setPriority(convertNumberToPriority(taskPriority));
                    return task;
                } else {
                    throw new ServerUnavilableException("Task not found with ID: " + taskId, HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error retrieving task: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Task deleteTaskById(int taskId) throws ServerUnavilableException {
        try {
            Task taskToDelete = getTaskById(taskId); // Retrieve the task before deletion
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY);
            preparedStatement.setInt(1, taskId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return taskToDelete; // Return the deleted task details
            } else {
                throw new ServerUnavilableException("Task deletion failed, no rows affected.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error deleting task: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Task updateTask(Task task) throws ServerUnavilableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY);
            preparedStatement.setString(1, task.getTaskName());
            preparedStatement.setDate(2, Date.valueOf(task.getDueDate()));
            preparedStatement.setTime(3, Time.valueOf(task.getDueTime()));
            preparedStatement.setInt(4, convertPriorityToNumber(task.getPriority()));
            preparedStatement.setBoolean(5, task.isCompleted());
            preparedStatement.setInt(6, task.getTaskId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return task;
            } else {
                throw new ServerUnavilableException("Task update failed, no rows affected.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error updating task: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Task> getAllTasks(int userId, String searchTerm, String priority, String sortBy, String sortOrder) throws ServerUnavilableException, TaskNotFoundException {
        List<Task> tasks = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM TASKS");
        boolean whereAdded = false;

        if (userId > 0) {
            queryBuilder.append(" WHERE USER_ID = ?");
            whereAdded = true;
        }

        if (priority != null && !priority.isEmpty()) {
            queryBuilder.append(whereAdded ? " AND" : " WHERE").append(" PRIORITY = ?");
            whereAdded = true;
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            queryBuilder.append(whereAdded ? " AND" : " WHERE").append(" TASK_NAME LIKE ?");
            whereAdded = true;
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            queryBuilder.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder != null ? sortOrder : "ASC");
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString());

            int paramIndex = 1;
            if (userId > 0) {
                preparedStatement.setInt(paramIndex++, userId);
            }

            if (priority != null && !priority.isEmpty()) {
                int priorityValue = convertPriorityToNumber(priority);
                preparedStatement.setInt(paramIndex++, priorityValue);
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                preparedStatement.setString(paramIndex++, "%" + searchTerm + "%");
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Task task = new Task();
                    task.setTaskId(resultSet.getInt("TASK_ID"));
                    task.setUserId(resultSet.getInt("USER_ID"));
                    task.setTaskName(resultSet.getString("TASK_NAME"));
                    task.setDueDate(resultSet.getDate("DUE_DATE").toLocalDate());
                    task.setDueTime(resultSet.getTime("DUE_TIME").toLocalTime());
                    task.setCompleted(resultSet.getBoolean("COMPLETED"));
                    int taskPriority = resultSet.getInt("PRIORITY");
                    task.setPriority(convertNumberToPriority(taskPriority));
                    tasks.add(task);
                }
            }

            if (tasks.isEmpty()) {
                throw new TaskNotFoundException("No tasks found for the given criteria.", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error retrieving tasks: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return tasks;
    }


    private int convertPriorityToNumber(String priority) {
        switch (priority.toLowerCase()) {
            case "high":
                return 1;
            case "medium":
                return 2;
            case "low":
                return 3;
            default:
                return 3;
        }
    }

    private String convertNumberToPriority(int priority) {
        switch (priority) {
            case 1:
                return "high";
            case 2:
                return "medium";
            case 3:
                return "low";
            default:
                return "low";
        }
    }
}