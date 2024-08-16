package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryEmployeeRepository implements EmployeeRepository {
    private static Map<Integer, Employee> employeeMap = new HashMap<>();

    @Override
    public Employee getEmployeeById(int empId) {
        return employeeMap.get(empId);
    }

    @Override
    public boolean addEmployee(Employee employee) {
        return employeeMap.put(employee.getEmpId(), employee) != null;
    }

    @Override
    public List<Employee> getAllEmployeeDetails() {
        return new ArrayList<>(employeeMap.values());
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        if (employeeMap.containsKey(employee.getEmpId())) {
            employeeMap.put(employee.getEmpId(), employee);
            return employee;
        }
        return null;
    }

    @Override
    public Employee deleteEmployee(int empId) {
        return employeeMap.remove(empId);
    }

    @Override
    public boolean isEmployeeExists(int empId) {
        return employeeMap.containsKey(empId);
    }
}
