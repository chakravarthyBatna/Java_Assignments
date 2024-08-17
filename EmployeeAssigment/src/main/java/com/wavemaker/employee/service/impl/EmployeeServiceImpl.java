package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.constant.Storage_Type;
import com.wavemaker.employee.factory.AddressRepositoryFactory;
import com.wavemaker.employee.factory.EmployeeRepositoryFactory;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private static EmployeeRepository employeeRepository;
    private static AddressRepository addressRepository;

    public EmployeeServiceImpl(Storage_Type storageType) {
        employeeRepository = EmployeeRepositoryFactory.getEmployeeRepositoryInstance(storageType); //calling the employee factory;
        addressRepository = AddressRepositoryFactory.getAddressRepositoryInstance(storageType);
    }

    @Override
    public Employee getEmployeeById(int empId) {
        Employee employee = employeeRepository.getEmployeeById(empId);
        Address address = addressRepository.getAddressByEmpId(empId);
        if (address != null) {
            employee.setAddress(address);
        }
        return employee;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        if (employee.getAddress() != null) {
            addressRepository.addAddress(employee.getAddress());
        }
        return employeeRepository.addEmployee(employee);
    }

    @Override
    public List<Employee> getAllEmployeeDetails() {
        List<Employee> employees = employeeRepository.getAllEmployeeDetails();
        for (Employee employee : employees) {
            Address address = addressRepository.getAddressByEmpId(employee.getEmpId());
            if (address != null) {
                employee.setAddress(address);
            }
        }
        return employees;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        if (employee.getAddress() != null) {
            addressRepository.updateAddress(employee.getAddress());
        }
        return employeeRepository.updateEmployee(employee);
    }

    @Override
    public Employee deleteEmployee(int empId) {
        Employee employee = getEmployeeById(empId);
        if (employee.getAddress() != null) {
            addressRepository.deleteAddressByEmpId(empId);
        }
        return employeeRepository.deleteEmployee(empId);
    }

    @Override
    public boolean isEmployeeExists(int empId) {
        return employeeRepository.isEmployeeExists(empId);
    }
}
