package com.wavemaker.todo.factory;

import com.wavemaker.todo.repository.TaskRepository;
import com.wavemaker.todo.repository.impl.TaskRepositoryImpl;

import java.sql.SQLException;

public class TaskRepositorySingleInstanceHandler {
    private TaskRepositorySingleInstanceHandler() {}

    private static volatile TaskRepository inDbRepository = null;

    public static TaskRepository getInDbTaskRepositoryInstance() throws SQLException {
        if (inDbRepository == null) {
            synchronized (TaskRepositorySingleInstanceHandler.class) {
                if (inDbRepository == null) {
                    inDbRepository = new TaskRepositoryImpl();
                }
            }
        }
        return inDbRepository;
    }
}
