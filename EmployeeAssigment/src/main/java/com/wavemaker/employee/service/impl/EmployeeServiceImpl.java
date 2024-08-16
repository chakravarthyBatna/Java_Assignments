package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.constant.Storage_Type;
import com.wavemaker.employee.factory.EmployeeRepositoryFactory;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(Storage_Type storageType) {
        employeeRepository = EmployeeRepositoryFactory.getEmployeeRepositoryInstance(storageType); //calling the employee factory;
    }

    @Override
    public Employee getEmployeeById(int empId) {
        return employeeRepository.getEmployeeById(empId);
    }

    @Override
    public boolean addEmployee(Employee employee) {
        return employeeRepository.addEmployee(employee);
    }

    @Override
    public List<Employee> getAllEmployeeDetails() {
        return employeeRepository.getAllEmployeeDetails();
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.updateEmployee(employee);
    }

    @Override
    public Employee deleteEmployee(int empId) {
        return employeeRepository.deleteEmployee(empId);
    }

    @Override
    public boolean isEmployeeExists(int empId) {
        return employeeRepository.isEmployeeExists(empId);
    }
}
