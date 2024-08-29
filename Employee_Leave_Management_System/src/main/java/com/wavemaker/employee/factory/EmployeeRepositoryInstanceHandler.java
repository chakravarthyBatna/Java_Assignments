package com.wavemaker.employee.factory;

import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.repository.impl.EmployeeRepositoryImpl;

import java.sql.SQLException;

public class EmployeeRepositoryInstanceHandler {
    private static EmployeeRepository employeeRepository = null;

    public static EmployeeRepository getEmployeeRepositoryInstance() throws SQLException {
        if (employeeRepository == null) {
            synchronized (EmployeeRepositoryInstanceHandler.class) {
                if (employeeRepository == null) {
                    employeeRepository = new EmployeeRepositoryImpl(); // Implementing EmployeeRepositoryImpl
                }
            }
        }
        return employeeRepository;
    }

}
