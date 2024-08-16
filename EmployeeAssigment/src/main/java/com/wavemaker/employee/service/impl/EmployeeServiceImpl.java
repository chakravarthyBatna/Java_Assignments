package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.repository.impl.InFileEmployeeRepository;
import com.wavemaker.employee.repository.impl.InMemoryEmployeeRepository;
import com.wavemaker.employee.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private int userChoice;
    public EmployeeServiceImpl(int userChoice) {
        if (userChoice == 1) {
            employeeRepository = new InMemoryEmployeeRepository();
        } else {
            employeeRepository = new InFileEmployeeRepository();
        }

    }
    @Override
    public Employee getEmployeeById(int empId) {
        return null;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        return false;
    }

    @Override
    public List<Employee> getAllEmployee() {
        return List.of();
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        return false;
    }

    @Override
    public boolean deleteEmployee(Employee employee) {
        return false;
    }
}
