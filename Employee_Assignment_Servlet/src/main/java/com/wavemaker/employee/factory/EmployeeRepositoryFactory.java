package com.wavemaker.employee.factory;

import com.wavemaker.employee.constant.StorageType;
import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.repository.EmployeeRepository;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EmployeeRepositoryFactory {
    private static final ConcurrentMap<StorageType, EmployeeRepository> repositoryMap = new ConcurrentHashMap<>();

    public static EmployeeRepository getEmployeeRepositoryInstance(StorageType storageType) throws FileCreationException, SQLException {
        if (repositoryMap.containsKey(storageType)) {
            return repositoryMap.get(storageType);
        }
        EmployeeRepository employeeRepository = null;
        if (storageType == StorageType.IN_MEMORY) {
            employeeRepository = EmployeeRepositoryInstanceHandler.getInMemoryEmployeeRepositoryInstance();
            repositoryMap.put(storageType, employeeRepository);
        } else if (storageType == StorageType.IN_FILE) {
            employeeRepository = EmployeeRepositoryInstanceHandler.getInFileEmployeeRepositoryInstance();
            repositoryMap.put(storageType, employeeRepository);
        } else if (storageType == StorageType.IN_DB) {
            employeeRepository = EmployeeRepositoryInstanceHandler.getInDbEmployeeRepositoryInstance();
        }
        return employeeRepository;
    }
}
