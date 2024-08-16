package com.wavemaker.employee.service;

import com.wavemaker.employee.model.Employee;
import java.util.List;
public interface EmployeeService {
    public Employee getEmployeeById(int empId);
    public boolean addEmployee(Employee employee);
    public List<Employee> getAllEmployee();
    public boolean updateEmployee(Employee employee);
    public boolean deleteEmployee(Employee employee);
}
