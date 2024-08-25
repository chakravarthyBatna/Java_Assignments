package com.wavemaker.todo.service.impl;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.exception.TaskNotFoundException;
import com.wavemaker.todo.factory.TaskRepositoryInstanceHandler;
import com.wavemaker.todo.pojo.Task;
import com.wavemaker.todo.repository.TaskRepository;
import com.wavemaker.todo.service.TaskService;

import java.sql.SQLException;
import java.util.List;

public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository = null;

    public TaskServiceImpl() throws SQLException {
        taskRepository = TaskRepositoryInstanceHandler.getInDbTaskRepositoryInstance();
    }

    @Override
    public Task markTaskAsCompleted(int userId, int taskId, boolean markAsComplete) throws ServerUnavilableException, TaskNotFoundException {
        return taskRepository.markTaskAsCompleted(userId, taskId, markAsComplete);
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
    public Task deleteTaskById(int userId, int taskId) throws ServerUnavilableException {
        return taskRepository.deleteTaskById(userId, taskId);
    }

    @Override
    public Task updateTask(int userId, Task task) throws ServerUnavilableException {
        return taskRepository.updateTask(userId, task);
    }

    @Override
    public List<Task> getAllTasks(int userId, String searchTerm, String priority, String sortBy, String sortOrder) throws ServerUnavilableException, TaskNotFoundException {
        return taskRepository.getAllTasks(userId, searchTerm, priority, sortBy, sortOrder);
    }

}
