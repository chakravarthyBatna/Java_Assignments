package com.wavemaker.todo.service;

import com.wavemaker.todo.exception.ServerUnavilableException;
import com.wavemaker.todo.pojo.Task;

import java.util.List;

public interface TaskService {
    public Task addTask(Task task) throws ServerUnavilableException;
    public Task getTaskById(int taskId);
    public Task deleteTaskById(int taskId);
    public Task updateTask(Task task);
    public List<Task> getAllTasks(int userId) throws ServerUnavilableException;

}
