package com.wavemaker.employee.controller;

import com.wavemaker.employee.constant.StorageType;
import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.exception.employee.EmployeeFileDeletionException;
import com.wavemaker.employee.exception.employee.EmployeeFileReadException;
import com.wavemaker.employee.exception.employee.EmployeeFileUpdateException;
import com.wavemaker.employee.exception.employee.EmployeeNotFoundException;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.service.AddressService;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.AddressServiceImpl;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private static EmployeeService employeeService;
    private static AddressService addressService;
    private StorageType storageType;

    public EmployeeController(StorageType storageType) throws FileCreationException, SQLException {
        employeeService = new EmployeeServiceImpl(storageType);
        addressService = new AddressServiceImpl(storageType);
    }

    public Employee addEmployee(Employee employee) throws ServerUnavilableException, EmployeeFileReadException {
        Employee addedEmployee = null;
        addedEmployee = employeeService.addEmployee(employee);
        return addedEmployee;
    }

    public Employee getEmployeeById(int empId) throws EmployeeFileReadException, ServerUnavilableException {
        Employee employee = employeeService.getEmployeeById(empId);
        return employee;
    }

    public List<Employee> getAllEmployees() throws EmployeeFileReadException, ServerUnavilableException {
        List<Employee> employeeList = employeeService.getAllEmployeeDetails();
        return employeeList;
    }

    public Employee updateEmployee(Employee employee) throws EmployeeFileUpdateException, EmployeeNotFoundException, ServerUnavilableException {
        Employee updatedEmployee = employeeService.updateEmployee(employee);
        return updatedEmployee;
    }

    public Employee deleteEmployeeById(int empId) throws EmployeeFileDeletionException, EmployeeFileUpdateException, EmployeeFileReadException, EmployeeNotFoundException, ServerUnavilableException {
        Employee deleteEmployee = employeeService.deleteEmployee(empId);
        return deleteEmployee;
    }

    public boolean isEmployeeExists(int empId) throws ServerUnavilableException {
        boolean isEmployeeExists = employeeService.isEmployeeExists(empId);
        return isEmployeeExists;
    }

    public Address getAddressByEmployeeId(int empId) throws ServerUnavilableException {
        Address address = addressService.getAddressByEmpId(empId);
        return address;
    }

    public Address deleteAddressByEmployeeId(int empId) throws ServerUnavilableException {
        Address deletedAddress = addressService.deleteAddressByEmpId(empId);
        return deletedAddress;
    }

    public Address addAddressByEmployeeId(Address address) throws ServerUnavilableException {
        Address addedAddress = addressService.addAddress(address);

        return addedAddress;
    }

    public Address updateAddressByEmployeeId(Address address) throws ServerUnavilableException {
        Address updatedAddress = addressService.updateAddress(address);
        return updatedAddress;
    }
}