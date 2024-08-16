package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;

import java.util.List;

public class InMemoryEmployeeRepository implements EmployeeRepository {
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
