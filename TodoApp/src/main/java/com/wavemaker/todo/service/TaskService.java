package com.wavemaker.todo.service;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.Task;

import java.util.List;

public interface TaskService {
    public Task addTask(Task task) throws ServerUnavilableException;
    public Task getTaskById(int taskId) throws ServerUnavilableException;
    public Task deleteTaskById(int taskId) throws ServerUnavilableException;
    public Task updateTask(Task task) throws ServerUnavilableException;
    public List<Task> getAllTasks(int userId) throws ServerUnavilableException;

}
