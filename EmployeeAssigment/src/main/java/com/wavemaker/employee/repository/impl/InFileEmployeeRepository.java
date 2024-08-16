package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;

import java.io.File;
import java.util.List;

public class InFileEmployeeRepository implements EmployeeRepository {

    @Override
    public Employee getEmployeeById(int empId) {
        return null;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        return false;
    }

    @Override
    public List<Employee> getAllEmployeeDetails() {
        return List.of();
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        return null;
    }

    @Override
    public Employee deleteEmployee(int empId) {
        return null;
    }

    @Override
    public boolean isEmployeeExists(int empId) {
        return false;
    }
}
