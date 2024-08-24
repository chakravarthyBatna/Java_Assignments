package com.wavemaker.todo.service.impl;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.factory.TaskRepositorySingleInstanceHandler;
import com.wavemaker.todo.pojo.Task;
import com.wavemaker.todo.repository.TaskRepository;
import com.wavemaker.todo.service.TaskService;

import java.sql.SQLException;
import java.util.List;

public class TaskServiceImpl implements TaskService {
    private static TaskRepository taskRepository = null;
    public TaskServiceImpl() throws SQLException {
        taskRepository = TaskRepositorySingleInstanceHandler.getInDbTaskRepositoryInstance();
    }
    @Override
    public Task addTask(Task task) throws ServerUnavilableException {
        return taskRepository.addTask(task);
    }

    @Override
    public Task getTaskById(int taskId) throws ServerUnavilableException {
        return taskRepository.getTaskById(taskId);
    }

    @Override
    public Task deleteTaskById(int taskId) throws ServerUnavilableException {
        return taskRepository.deleteTaskById(taskId);
    }

    @Override
    public Task updateTask(Task task) throws ServerUnavilableException {
        return taskRepository.updateTask(task);
    }

    @Override
    public List<Task> getAllTasks(int userId) throws ServerUnavilableException {
        return taskRepository.getAllTasks(userId);
    }

    @Override
    public List<Task> getAllTasksByPriority(int userId, String priority) throws ServerUnavilableException {
        return taskRepository.getAllTasksByPriority(userId, priority);
    }

    @Override
    public List<Task> getAllTasksByDueDate(int userId, String order) throws ServerUnavilableException {
        return taskRepository.getAllTasksByDueDate(userId,order);
    }

    @Override
    public List<Task> getAllTasksByPriorityOrder(int userId, String order) throws ServerUnavilableException {
        return taskRepository.getAllTasksByPriorityOrder(userId,order);
    }
}
