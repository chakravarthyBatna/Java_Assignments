package com.wavemaker.employee.repository.impl.inmemory;

import com.wavemaker.employee.exception.employee.EmployeeNotFoundException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EmployeeRepositoryInMemoryImpl implements EmployeeRepository {
    private static final Map<Integer, Employee> employeeMap = new ConcurrentHashMap<>();
    private static final Map<String, Employee> employeeEmailMap = new ConcurrentHashMap<>();
    private static int maxEmployeeId = 0;
    @Override
    public Employee getEmployeeById(int empId) {
        return employeeMap.get(empId);
    }

    @Override
    public Employee addEmployee(Employee employee) {
        if (maxEmployeeId == 0) {
            maxEmployeeId = generateEmpId();
        } else {
            maxEmployeeId += 1;
        }
        employee.setEmpId(maxEmployeeId);
        employeeEmailMap.put(employee.getEmail(),employee);
        employeeMap.put(employee.getEmpId(), employee);
        return employee;

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
        employeeEmailMap.remove(removedEmployee.getEmail());
        if (removedEmployee == null) {
            throw new EmployeeNotFoundException("Employee with Id: " + empId + " Not Found To Delete", 404);
        }
        return removedEmployee;
    }

    @Override
    public Employee getEmployeeByEmail(String email) {
        return employeeEmailMap.get(email);
    }

    @Override
    public boolean isEmployeeExists(int empId) {
        return employeeMap.containsKey(empId);
    }

    private int generateEmpId() {
        for (Integer id : employeeMap.keySet()) {
            maxEmployeeId = Math.max(id,maxEmployeeId);
        }
        return maxEmployeeId + 1;
    }
}
