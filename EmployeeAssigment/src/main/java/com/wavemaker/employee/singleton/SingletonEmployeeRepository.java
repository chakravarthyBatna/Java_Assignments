package com.wavemaker.employee.singleton;

import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.repository.impl.InFileEmployeeRepository;
import com.wavemaker.employee.repository.impl.InMemoryEmployeeRepository;

public class SingletonEmployeeRepository {
    private SingletonEmployeeRepository() {}

    private static volatile EmployeeRepository inMemoryRepository;
    private static volatile EmployeeRepository inFileRepository;

    public static EmployeeRepository getInMemoryEmployeeRepositoryInstance() {
        if (inMemoryRepository == null) {
            synchronized (SingletonEmployeeRepository.class) {
                if (inMemoryRepository == null) {
                    inMemoryRepository = new InMemoryEmployeeRepository();
                }
            }
        }
        return inMemoryRepository;
    }

    public static EmployeeRepository getInFileEmployeeRepositoryInstance() {
        if (inFileRepository == null) {
            synchronized (SingletonEmployeeRepository.class) {
                if (inFileRepository == null) {
                    inFileRepository = new InFileEmployeeRepository();
                }
            }
        }
        return inFileRepository;
    }
}
