package com.wavemaker.employee.repository.impl.inmemory;

import com.wavemaker.employee.exception.employee.DuplicateEmployeeRecordFoundException;
import com.wavemaker.employee.exception.employee.EmployeeNotFoundException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EmployeeRepositoryInMemoryImpl implements EmployeeRepository {
    private static final ConcurrentHashMap<Integer,Employee> employeeMap = new ConcurrentHashMap<>();

    @Override
    public Employee getEmployeeById(int empId) {
        return employeeMap.get(empId);
    }

    @Override
    public boolean addEmployee(Employee employee) throws DuplicateEmployeeRecordFoundException {
        if (employeeMap.containsKey(employee.getEmpId())) {
            throw new DuplicateEmployeeRecordFoundException("Employee With Id : " + employee.getEmpId() + " Already Exists", 409); //409= conflit;
        } else {
            employeeMap.put(employee.getEmpId(), employee);
            return true;
        }
    }

    @Override
    public List<Employee> getAllEmployeeDetails() {
        return new ArrayList<>(employeeMap.values());
    }

    @Override
    public Employee updateEmployee(Employee employee) throws EmployeeNotFoundException {
        if (employeeMap.containsKey(employee.getEmpId())) {
            employeeMap.put(employee.getEmpId(), employee);
            return employee;
        } else {
            throw new EmployeeNotFoundException("Employee with Id : " + employee.getEmpId() + " Not Found To Update", 404); //404 = not found
        }
    }

    @Override
    public Employee deleteEmployee(int empId) throws EmployeeNotFoundException {
        Employee removedEmployee = employeeMap.remove(empId);
        if (removedEmployee == null) {
            throw new EmployeeNotFoundException("Employee with Id: " + empId + " Not Found To Delete", 404);
        }
        return removedEmployee;
    }

    @Override
    public boolean isEmployeeExists(int empId) {
        return employeeMap.containsKey(empId);
    }

    @Override
    public int getCount() {
        return employeeMap.size();
    }

}
