package com.wavemaker.employee.service;

import com.wavemaker.employee.model.Employee;
import java.util.List;
public interface EmployeeService {
    public Employee getEmployeeById(int empId);
    public boolean addEmployee(Employee employee);
    public List<Employee> getAllEmployeeDetails();
    public Employee updateEmployee(Employee employee);
    public Employee deleteEmployee(int empId);
    public boolean isEmployeeExists(int empId);
}
