package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.exception.EmployeeFileReadException;
import com.wavemaker.employee.exception.EmployeeFileWriteException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.util.CSVFileReaderAndWriter;
import com.wavemaker.employee.util.FileCreateUtil;

import java.util.List;

public class InFileEmployeeRepository implements EmployeeRepository {
    private static final String FILE_PATH = "C:\\Users\\chakraarthyb_700063\\IdeaProjects\\Java_Assignments\\Employees.txt";
    private CSVFileReaderAndWriter csvFileReaderAndWriter;

    public InFileEmployeeRepository() {
        csvFileReaderAndWriter = new CSVFileReaderAndWriter(FileCreateUtil.createFileIfNotExists(FILE_PATH));
    }

    @Override
    public Employee getEmployeeById(int empId) {
        return null;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        boolean isOperationSuccess = false;
        try {
            isOperationSuccess = csvFileReaderAndWriter.writeEmployee(employee);
        } catch (EmployeeFileWriteException exception) {
            throw new ServerUnavilableException("Server Unavailable while adding the employee", 500);
        }
        return isOperationSuccess;
    }

    @Override
    public List<Employee> getAllEmployeeDetails() {
        return List.of();
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        return null;
    }

    @Override
    public Employee deleteEmployee(int empId) {
        return null;
    }

    @Override
    public boolean isEmployeeExists(int empId) {
        boolean isOperationSuccess = false;
        try {
            isOperationSuccess = csvFileReaderAndWriter.isEmployeeExists(empId);

        } catch (EmployeeFileReadException e) {
            throw new ServerUnavilableException("Server Unavailable while Reading the employee", 500);
        }
    }
}
