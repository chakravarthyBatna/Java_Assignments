package com.wavemaker.todo.repository;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.Task;

import java.util.List;

public interface TaskRepository {
    public Task addTask(Task task) throws ServerUnavilableException;

    public Task getTaskById(int taskId) throws ServerUnavilableException;

    public Task deleteTaskById(int taskId) throws ServerUnavilableException;

    public Task updateTask(Task task) throws ServerUnavilableException;

    public List<Task> getAllTasks(int userId) throws ServerUnavilableException;

    public List<Task> getAllTasksByPriority(int userId, String priority) throws ServerUnavilableException;

    public List<Task> getAllTasksByDueDate(int userId, String order) throws ServerUnavilableException;
    public List<Task> getAllTasksByPriorityOrder(int userId, String order) throws ServerUnavilableException;
}
