package com.wavemaker.employee.factory;

import com.wavemaker.employee.constant.Storage_Type;
import com.wavemaker.employee.repository.EmployeeRepository;

import java.util.HashMap;
import java.util.Map;

public class EmployeeRepositoryFactory {
    private static EmployeeRepository employeeRepository;
    private static final Map<Storage_Type, EmployeeRepository> repositoryMap = new HashMap<>();

    public static EmployeeRepository getEmployeeRepositoryInstance(Storage_Type storageType) {
        if (repositoryMap.containsKey(storageType)) {
            return repositoryMap.get(storageType);
        }
        if (storageType == Storage_Type.IN_MEMORY) {
            employeeRepository = EmployeeRepositoryInstanceHandler.getInMemoryEmployeeRepositoryInstance();
            repositoryMap.put(storageType, employeeRepository);
        } else if (storageType == Storage_Type.IN_FILE) {
            employeeRepository = EmployeeRepositoryInstanceHandler.getInFileEmployeeRepositoryInstance();
            repositoryMap.put(storageType, employeeRepository);
        }
        return repositoryMap.get(storageType);
    }
}
