package com.wavemaker.employee.repository;

import com.wavemaker.employee.exception.DuplicateEmployeeRecordFoundException;
import com.wavemaker.employee.model.Employee;

import java.util.List;

public interface EmployeeRepository {
    public Employee getEmployeeById(int empId);
    public boolean addEmployee(Employee employee) throws DuplicateEmployeeRecordFoundException;
    public List<Employee> getAllEmployeeDetails();
    public Employee updateEmployee(Employee employee);
    public Employee deleteEmployee(int empId);
    public boolean isEmployeeExists(int empId);
}
