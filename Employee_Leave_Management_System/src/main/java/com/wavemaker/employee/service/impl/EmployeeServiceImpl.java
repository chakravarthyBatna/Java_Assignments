package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.factory.EmployeeRepositoryInstanceHandler;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.dto.EmployeeVO;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.service.EmployeeService;

import java.sql.SQLException;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository = null;

    public EmployeeServiceImpl() throws SQLException {
        employeeRepository = EmployeeRepositoryInstanceHandler.getEmployeeRepositoryInstance();
    }

    @Override
    public EmployeeVO getEmployeeById(int empId) throws ServerUnavilableException {
        return employeeRepository.getEmployeeById(empId);
    }

    @Override
    public Employee addEmployee(Employee employee) {
        return employeeRepository.addEmployee(employee);
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
    public List<Employee> getEmployees() {
        return employeeRepository.getEmployees();
    }

    @Override
    public List<Employee> getAllManagers() {
        return employeeRepository.getAllManagers();
    }
}
