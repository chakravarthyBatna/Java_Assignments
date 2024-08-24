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
    private static final String LIST_QUERY = "SELECT TASK_ID, TASK_NAME, DUE_DATE," +
            " DUE_TIME, PRIORITY, COMPLETED FROM TASKS WHERE USER_ID = ? ORDER BY CASE PRIORITY" +
            " WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 ELSE 4 END";

    private static final String LIST_QUERY_BY_PRIORITY = "SELECT TASK_ID, TASK_NAME, DUE_DATE, " +
            "DUE_TIME, PRIORITY, COMPLETED FROM TASKS WHERE USER_ID = ? AND PRIORITY = ? " +
            "AND COMPLETED = false ORDER BY DUE_DATE, DUE_TIME";
    private static final String LIST_QUERY_BY_DUE_DATE = "SELECT TASK_ID, USER_ID, TASK_NAME, DUE_DATE, DUE_TIME, PRIORITY, COMPLETED FROM TASKS WHERE USER_ID = ? ORDER BY DUE_DATE ";
    private static final String LIST_QUERY_BY_PRIORITY_ORDER = "SELECT TASK_ID, TASK_NAME, DUE_DATE, DUE_TIME, PRIORITY, COMPLETED FROM TASKS WHERE USER_ID = ? ORDER BY CASE PRIORITY WHEN 'low' THEN 1 WHEN 'medium' THEN 2 WHEN 'high' THEN 3 END ";
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
    public List<Task> getAllTasks(int userId) throws ServerUnavilableException {
        List<Task> tasks = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(LIST_QUERY);
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Task task = new Task();
                    task.setTaskId(resultSet.getInt("TASK_ID"));
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
    public List<Task> getAllTasksByPriority(int userId, String priority) throws ServerUnavilableException {
        List<Task> taskList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(LIST_QUERY_BY_PRIORITY);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, priority.toLowerCase());

            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Task task = new Task();
                    task.setTaskId(resultSet.getInt("TASK_ID"));
                    task.setTaskName(resultSet.getString("TASK_NAME"));
                    task.setDueDate(resultSet.getDate("DUE_DATE").toLocalDate());
                    task.setDueTime(resultSet.getTime("DUE_TIME").toLocalTime());
                    task.setPriority(resultSet.getString("PRIORITY"));
                    task.setCompleted(resultSet.getBoolean("COMPLETED")); // Add this line to set the completed status
                    taskList.add(task);
                }
            } catch (SQLException e) {
                throw new ServerUnavilableException("Unable to fetch tasks from database", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new ServerUnavilableException("Error retrieving tasks by priority: " + e.getMessage(), 500);
        }

        return taskList;
    }

    @Override
    public List<Task> getAllTasksByDueDate(int userId, String order) throws ServerUnavilableException {
        List<Task> tasks = new ArrayList<>();
        try {
            String query = LIST_QUERY_BY_DUE_DATE + (order.equals("asc") ? "ASC, DUE_TIME ASC" : "DESC, DUE_TIME DESC");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Task task = new Task();
                    task.setTaskId(resultSet.getInt("TASK_ID"));
                    task.setTaskName(resultSet.getString("TASK_NAME"));
                    task.setDueDate(resultSet.getDate("DUE_DATE").toLocalDate());
                    task.setDueTime(resultSet.getTime("DUE_TIME").toLocalTime());
                    task.setPriority(resultSet.getString("PRIORITY"));
                    task.setCompleted(resultSet.getBoolean("COMPLETED"));

                    tasks.add(task);
                }
            } catch (Exception e) {
                throw new ServerUnavilableException("Error fetching tasks by due date", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error fetching tasks by due date", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return tasks;
    }

    @Override
    public List<Task> getAllTasksByPriorityOrder(int userId, String order) throws ServerUnavilableException {
        List<Task> taskList = new ArrayList<Task>();
        String query = LIST_QUERY_BY_PRIORITY_ORDER + (order.equals("asc") ? "ASC" : "DESC");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Task task = new Task();
                    task.setTaskId(resultSet.getInt("TASK_ID"));
                    task.setTaskName(resultSet.getString("TASK_NAME"));
                    task.setDueDate(resultSet.getDate("DUE_DATE").toLocalDate());
                    task.setDueTime(resultSet.getTime("DUE_TIME").toLocalTime());
                    task.setPriority(resultSet.getString("PRIORITY"));
                    task.setCompleted(resultSet.getBoolean("COMPLETED"));
                    if (!task.isCompleted()) {
                        taskList.add(task);
                    }
                }
            } catch (Exception e) {
                throw new ServerUnavilableException("Error fetching tasks by priority order", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error fetching tasks by priority order", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return taskList;
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
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_QUERY);
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
            preparedStatement.setString(4, task.getPriority());
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
}