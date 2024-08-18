package com.wavemaker.employee.repository;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.exception.employee.*;
import com.wavemaker.employee.pojo.Employee;

import java.util.List;

public interface EmployeeRepository {
    public Employee getEmployeeById(int empId) throws EmployeeFileReadException;
    public boolean addEmployee(Employee employee) throws DuplicateEmployeeRecordFoundException, ServerUnavilableException;
    public List<Employee> getAllEmployeeDetails() throws EmployeeFileReadException;
    public Employee updateEmployee(Employee employee) throws EmployeeNotFoundException, EmployeeFileUpdateException;
    public Employee deleteEmployee(int empId) throws EmployeeNotFoundException, EmployeeFileDeletionException, EmployeeFileUpdateException;
    public boolean isEmployeeExists(int empId) throws ServerUnavilableException;
    public int getCount() throws EmployeeFileReadException;
}
