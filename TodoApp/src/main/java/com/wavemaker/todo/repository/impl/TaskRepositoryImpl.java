package com.wavemaker.todo.repository.impl;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.Task;
import com.wavemaker.todo.repository.TaskRepository;
import com.wavemaker.todo.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepositoryImpl implements TaskRepository {
    private static Connection connection = null;
    private static final String LIST_QUERY = "SELECT TASK_ID, USER_ID, TASK_NAME, DUE_DATE," +
            " DUE_TIME, PRIORITY, COMPLETED FROM TASKS WHERE USER_ID = ? ORDER BY CASE PRIORITY" +
            " WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 ELSE 4 END";
    private static final String INSERT_QUERY = "INSERT INTO TASKS (USER_ID, TASK_NAME, DUE_DATE," +
            " DUE_TIME, PRIORITY, COMPLETED) VALUES (?, ?, ?, ?, ?, ?)";

    public TaskRepositoryImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    @Override
    public List<Task> getAllTasks(int userId) throws ServerUnavilableException {
        List<Task> tasks = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(LIST_QUERY);
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Task task = new Task();
                    task.setTaskId(resultSet.getInt("TASK_ID"));
                    task.setUserId(resultSet.getInt("USER_ID"));
                    task.setTaskName(resultSet.getString("TASK_NAME"));
                    task.setDueDate(resultSet.getDate("DUE_DATE").toLocalDate());
                    task.setDueTime(resultSet.getTime("DUE_TIME").toLocalTime());
                    task.setPriority(resultSet.getString("PRIORITY"));
                    task.setCompleted(resultSet.getBoolean("COMPLETED"));
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Unable to Retrieve tasks", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return tasks;
    }


    @Override
    public Task addTask(Task task) throws ServerUnavilableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, task.getUserId());
            preparedStatement.setString(2, task.getTaskName());
            preparedStatement.setDate(3, Date.valueOf(task.getDueDate()));
            preparedStatement.setTime(4, Time.valueOf(task.getDueTime()));
            preparedStatement.setString(5, task.getPriority());
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
        String query = "SELECT TASK_ID, USER_ID, TASK_NAME, DUE_DATE, DUE_TIME, PRIORITY, COMPLETED FROM TASKS WHERE TASK_ID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, taskId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Task task = new Task();
                    task.setTaskId(resultSet.getInt("TASK_ID"));
                    task.setUserId(resultSet.getInt("USER_ID"));
                    task.setTaskName(resultSet.getString("TASK_NAME"));
                    task.setDueDate(resultSet.getDate("DUE_DATE").toLocalDate());
                    task.setDueTime(resultSet.getTime("DUE_TIME").toLocalTime());
                    task.setPriority(resultSet.getString("PRIORITY"));
                    task.setCompleted(resultSet.getBoolean("COMPLETED"));
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
        String query = "DELETE FROM TASKS WHERE TASK_ID = ?";
        try {
            Task taskToDelete = getTaskById(taskId); // Retrieve the task before deletion
            PreparedStatement preparedStatement = connection.prepareStatement(query);
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
        String query = "UPDATE TASKS SET TASK_NAME = ?, DUE_DATE = ?, DUE_TIME = ?, PRIORITY = ?, COMPLETED = ? WHERE TASK_ID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, task.getTaskName());
            preparedStatement.setDate(2, Date.valueOf(task.getDueDate()));
            preparedStatement.setTime(3, Time.valueOf(task.getDueTime()));
            preparedStatement.setString(4, task.getPriority());
            preparedStatement.setBoolean(5, task.isCompleted());
            preparedStatement.setInt(6, task.getTaskId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return task; // Return the updated task details
            } else {
                throw new ServerUnavilableException("Task update failed, no rows affected.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error updating task: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}