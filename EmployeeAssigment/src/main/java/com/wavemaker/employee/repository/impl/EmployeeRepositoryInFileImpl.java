package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.exception.employee.EmployeeFileReadException;
import com.wavemaker.employee.exception.employee.EmployeeFileWriteException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.util.EmployeeCSVFileReaderAndWriter;
import com.wavemaker.employee.util.FileCreateUtil;

import java.util.List;

public class EmployeeRepositoryInFileImpl implements EmployeeRepository {
    private static final String FILE_PATH = "C:\\Users\\chakraarthyb_700063\\IdeaProjects\\Java_Assignments\\Employees.txt";
    private final EmployeeCSVFileReaderAndWriter employeeCsvFileReaderAndWriter;

    public EmployeeRepositoryInFileImpl() {
        employeeCsvFileReaderAndWriter = new EmployeeCSVFileReaderAndWriter(FileCreateUtil.createFileIfNotExists(FILE_PATH));
    }

    @Override
    public Employee getEmployeeById(int empId) {
        return employeeCsvFileReaderAndWriter.readEmployeeByEmpId(empId);
    }

    @Override
    public boolean addEmployee(Employee employee) {
        boolean isOperationSuccess = false;
        try {
            isOperationSuccess = employeeCsvFileReaderAndWriter.writeEmployee(employee);
        } catch (EmployeeFileWriteException exception) {
            throw new ServerUnavilableException("Server Unavailable while adding the employee", 500);
        }
        return isOperationSuccess;
    }

    @Override
    public List<Employee> getAllEmployeeDetails() {
        return employeeCsvFileReaderAndWriter.readAllEmployees();
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        return employeeCsvFileReaderAndWriter.updateEmployee(employee);
    }

    @Override
    public Employee deleteEmployee(int empId) {
        return employeeCsvFileReaderAndWriter.deleteEmployee(empId);
    }

    @Override
    public boolean isEmployeeExists(int empId) throws ServerUnavilableException {
        boolean isOperationSuccess = false;
        try {
            isOperationSuccess = employeeCsvFileReaderAndWriter.isEmployeeExists(empId);

        } catch (EmployeeFileReadException e) {
            throw new ServerUnavilableException("Server Unavailable while Reading the employee", 500);
        }
        return isOperationSuccess;
    }
}
