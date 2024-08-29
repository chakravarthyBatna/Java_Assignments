package com.wavemaker.employee.service;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.dto.EmployeeVO;

import java.util.List;

public interface EmployeeService {
    public EmployeeVO getEmployeeById(int empId) throws ServerUnavilableException;

    public Employee addEmployee(Employee employee);

    public Employee updateEmployee(Employee employee);

    public Employee deleteEmployee(int empId);

    public List<Employee> getEmployees();

    public List<Employee> getAllManagers();
}
