package com.wavemaker.employee.service;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.exception.employee.EmployeeFileDeletionException;
import com.wavemaker.employee.exception.employee.EmployeeFileReadException;
import com.wavemaker.employee.exception.employee.EmployeeFileUpdateException;
import com.wavemaker.employee.exception.employee.EmployeeNotFoundException;
import com.wavemaker.employee.pojo.Employee;

import java.util.List;
public interface EmployeeService {
    public Employee getEmployeeById(int empId) throws EmployeeFileReadException, ServerUnavilableException;
    public Employee addEmployee(Employee employee) throws ServerUnavilableException, EmployeeFileReadException;
    public List<Employee> getAllEmployeeDetails() throws EmployeeFileReadException, ServerUnavilableException;
    public Employee updateEmployee(Employee employee) throws EmployeeFileUpdateException, EmployeeNotFoundException, ServerUnavilableException;
    public Employee deleteEmployee(int empId) throws EmployeeNotFoundException, EmployeeFileReadException, EmployeeFileDeletionException, EmployeeFileUpdateException, ServerUnavilableException;
    public boolean isEmployeeExists(int empId) throws ServerUnavilableException;
    public Employee searchByEmployeeEmail(String email) throws ServerUnavilableException;
}
