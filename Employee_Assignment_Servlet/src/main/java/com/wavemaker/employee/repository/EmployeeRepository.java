package com.wavemaker.employee.repository;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.Employee;

import java.util.List;

public interface EmployeeRepository {
    public Employee getEmployeeById(int empId) throws EmployeeFileReadException, ServerUnavilableException;
    public Employee addEmployee(Employee employee) throws ServerUnavilableException, EmployeeFileReadException;
    public List<Employee> getAllEmployeeDetails() throws EmployeeFileReadException;
    public Employee updateEmployee(Employee employee) throws EmployeeNotFoundException, EmployeeFileUpdateException, ServerUnavilableException;
    public Employee deleteEmployee(int empId) throws EmployeeNotFoundException, EmployeeFileDeletionException, EmployeeFileUpdateException, ServerUnavilableException;
    public Employee getEmployeeByEmail(String email) throws ServerUnavilableException;
    public boolean isEmployeeExists(int empId) throws ServerUnavilableException;
}
