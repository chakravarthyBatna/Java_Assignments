package com.wavemaker.employee.factory;

import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.repository.impl.file.EmployeeRepositoryInFileImpl;
import com.wavemaker.employee.repository.impl.indb.InDbEmployeeRepositoryImpl;
import com.wavemaker.employee.repository.impl.inmemory.EmployeeRepositoryInMemoryImpl;

import java.sql.SQLException;

public class EmployeeRepositoryInstanceHandler {
    private EmployeeRepositoryInstanceHandler() {}

    private static volatile EmployeeRepository inMemoryRepository = null;
    private static volatile EmployeeRepository inFileRepository = null;
    private static volatile EmployeeRepository inDbRepository = null;
    public static EmployeeRepository getInMemoryEmployeeRepositoryInstance() {
        if (inMemoryRepository == null) {
            synchronized (EmployeeRepositoryInstanceHandler.class) {
                if (inMemoryRepository == null) {
                    inMemoryRepository = new EmployeeRepositoryInMemoryImpl();
                }
            }
        }
        return inMemoryRepository;
    }

    public static EmployeeRepository getInFileEmployeeRepositoryInstance() throws FileCreationException {
        if (inFileRepository == null) {
            synchronized (EmployeeRepositoryInstanceHandler.class) {
                if (inFileRepository == null) {
                    inFileRepository = new EmployeeRepositoryInFileImpl();
                }
            }
        }
        return inFileRepository;
    }

    public static EmployeeRepository getInDbEmployeeRepositoryInstance() throws SQLException {
        if (inDbRepository == null) {
            synchronized (EmployeeRepositoryInstanceHandler.class) {
                if (inDbRepository == null) {
                    inDbRepository = new InDbEmployeeRepositoryImpl();
                }
            }
        }
        return inDbRepository;
    }
}
