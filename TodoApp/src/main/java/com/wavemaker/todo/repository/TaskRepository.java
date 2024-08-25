package com.wavemaker.todo.repository;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.exception.TaskNotFoundException;
import com.wavemaker.todo.pojo.Task;

import java.util.List;

public interface TaskRepository {
    public Task markTaskAsCompleted(int userId, int taskId, boolean markAsComplete) throws ServerUnavilableException, TaskNotFoundException;

    public Task addTask(Task task) throws ServerUnavilableException;

    public Task getTaskById(int taskId) throws ServerUnavilableException;

    public Task deleteTaskById(int userId,int taskId) throws ServerUnavilableException;

    public Task updateTask(int userId,Task task) throws ServerUnavilableException;

    public List<Task> getAllTasks(int userId,  String searchTerm, String priority, String sortBy, String sortOrder) throws ServerUnavilableException, TaskNotFoundException;
}
