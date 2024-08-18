package com.wavemaker.employee.factory;

import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.repository.impl.file.EmployeeRepositoryInFileImpl;
import com.wavemaker.employee.repository.impl.inmemory.EmployeeRepositoryInMemoryImpl;

public class EmployeeRepositoryInstanceHandler {
    private EmployeeRepositoryInstanceHandler() {}

    private static volatile EmployeeRepository inMemoryRepository = null;
    private static volatile EmployeeRepository inFileRepository = null;

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
}
