package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.constant.StorageType;
import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.exception.employee.EmployeeFileDeletionException;
import com.wavemaker.employee.exception.employee.EmployeeFileReadException;
import com.wavemaker.employee.exception.employee.EmployeeFileUpdateException;
import com.wavemaker.employee.exception.employee.EmployeeNotFoundException;
import com.wavemaker.employee.factory.AddressRepositoryFactory;
import com.wavemaker.employee.factory.EmployeeRepositoryFactory;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.service.EmployeeService;

import java.sql.SQLException;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private static EmployeeRepository employeeRepository;
    private static AddressRepository addressRepository;

    public EmployeeServiceImpl(StorageType storageType) throws FileCreationException, SQLException {
        employeeRepository = EmployeeRepositoryFactory.getEmployeeRepositoryInstance(storageType); //calling the employee factory;
        addressRepository = AddressRepositoryFactory.getAddressRepositoryInstance(storageType);
    }

    @Override
    public Employee getEmployeeById(int empId) throws EmployeeFileReadException, ServerUnavilableException {
        Employee employee = employeeRepository.getEmployeeById(empId);
        Address address = addressRepository.getAddressByEmpId(empId);
        if (address != null) {
            employee.setAddress(address);
        }
        return employee;
    }

    @Override
    public Employee addEmployee(Employee employee) throws ServerUnavilableException, EmployeeFileReadException {
        Employee addedEmployee = employeeRepository.addEmployee(employee);
        if (employee.getAddress() != null && addedEmployee != null) {
            Address address = addedEmployee.getAddress();
            address.setEmpId(addedEmployee.getEmpId());
            addressRepository.addAddress(address);
        }
        return employee;
    }

    @Override
    public List<Employee> getAllEmployeeDetails() throws EmployeeFileReadException, ServerUnavilableException {
        List<Employee> employees = employeeRepository.getAllEmployeeDetails();
        for (Employee employee : employees) {
            Address address = addressRepository.getAddressByEmpId(employee.getEmpId());
            employee.setAddress(address);
        }
        return employees;
    }

    @Override
    public Employee updateEmployee(Employee employee) throws EmployeeFileUpdateException, EmployeeNotFoundException, ServerUnavilableException {
        if (employee.getAddress() != null) {
            addressRepository.updateAddress(employee.getAddress());
        }
        return employeeRepository.updateEmployee(employee);
    }

    @Override
    public Employee deleteEmployee(int empId) throws EmployeeNotFoundException, EmployeeFileReadException, EmployeeFileDeletionException, EmployeeFileUpdateException, ServerUnavilableException {
        Employee employee = getEmployeeById(empId);
        if (employee.getAddress() != null) {
            addressRepository.deleteAddressByEmpId(empId);
        }
        return employeeRepository.deleteEmployee(empId);
    }

    @Override
    public boolean isEmployeeExists(int empId) throws ServerUnavilableException {
        return employeeRepository.isEmployeeExists(empId);
    }

    @Override
    public Employee searchByEmployeeEmail(String email) {
        return null;
    }
}
