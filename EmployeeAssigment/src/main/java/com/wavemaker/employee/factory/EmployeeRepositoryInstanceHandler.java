package com.wavemaker.employee.factory;

import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.repository.impl.EmployeeRepositoryInFileImpl;
import com.wavemaker.employee.repository.impl.EmployeeRepositoryInMemoryImpl;

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

    public static EmployeeRepository getInFileEmployeeRepositoryInstance() {
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
