package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.exception.DuplicateEmployeeRecordFoundException;
import com.wavemaker.employee.exception.EmployeeNotFoundException;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryEmployeeRepository implements EmployeeRepository {
    private static final Map<Integer, Employee> employeeMap = new HashMap<>();

    @Override
    public Employee getEmployeeById(int empId) {
        return employeeMap.get(empId);
    }

    @Override
    public boolean addEmployee(Employee employee) throws DuplicateEmployeeRecordFoundException {
        if (employeeMap.containsKey(employee.getEmpId())) {
            throw new DuplicateEmployeeRecordFoundException("Employee With Id : " + employee.getEmpId() + " Already Exists", 409); //409= conflit;
        } else {
            employeeMap.put(employee.getEmpId(), employee);
            return true;
        }
    }

    @Override
    public List<Employee> getAllEmployeeDetails() {
        return new ArrayList<>(employeeMap.values());
    }

    @Override
    public Employee updateEmployee(Employee employee) throws EmployeeNotFoundException {
        if (employeeMap.containsKey(employee.getEmpId())) {
            employeeMap.put(employee.getEmpId(), employee);
            return employee;
        } else {
            throw new EmployeeNotFoundException("Employee with Id : " + employee.getEmpId() + " Not Found To Update", 404); //404 = not found
        }
    }

    @Override
    public Employee deleteEmployee(int empId) throws EmployeeNotFoundException {
        Employee removedEmployee = employeeMap.remove(empId);
        if (removedEmployee == null) {
            throw new EmployeeNotFoundException("Employee with Id: " + empId + " Not Found To Delete", 404);
        }
        return removedEmployee;
    }

    @Override
    public boolean isEmployeeExists(int empId) {
        return employeeMap.containsKey(empId);
    }
}
