package com.wavemaker.employee.service;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.exception.employee.*;
import com.wavemaker.employee.pojo.Employee;
import java.util.List;
public interface EmployeeService {
    public Employee getEmployeeById(int empId) throws EmployeeFileReadException;
    public boolean addEmployee(Employee employee) throws ServerUnavilableException, DuplicateEmployeeRecordFoundException;
    public List<Employee> getAllEmployeeDetails() throws EmployeeFileReadException;
    public Employee updateEmployee(Employee employee) throws EmployeeFileUpdateException, EmployeeNotFoundException;
    public Employee deleteEmployee(int empId) throws EmployeeNotFoundException, EmployeeFileReadException, EmployeeFileDeletionException, EmployeeFileUpdateException;
    public boolean isEmployeeExists(int empId) throws ServerUnavilableException;
}
