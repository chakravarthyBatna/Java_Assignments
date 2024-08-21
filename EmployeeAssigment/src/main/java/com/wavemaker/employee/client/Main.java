package com.wavemaker.employee.client;

import com.wavemaker.employee.constant.StorageType;
import com.wavemaker.employee.controller.EmployeeController;
import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.exception.InvalidUserInputException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.exception.employee.EmployeeFileDeletionException;
import com.wavemaker.employee.exception.employee.EmployeeFileReadException;
import com.wavemaker.employee.exception.employee.EmployeeFileUpdateException;
import com.wavemaker.employee.exception.employee.EmployeeNotFoundException;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.pojo.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final ClientInputOutputCLI clientInputOutputCLI = new ClientInputOutputCLI();;
    private static EmployeeController employeeController = null;

    public static void main(String[] args) throws ServerUnavilableException {
        boolean exit = false;
        createEmployeeControllerInstance();  // goto method declaration for comments;
        int userInput = -1;
        while (!exit) {
            userInput = fetchUserInput();
            switch (userInput) {
                case 1:
                    try {
                        Employee employee = addEmployee();
                        System.out.println("Employee added");
                        clientInputOutputCLI.printEmployee(employee);
                        logger.info("Employee Details : {}", employee);
                    } catch (ServerUnavilableException | EmployeeFileReadException e) {
                        logger.error("Exception ; ", e);
                    }
                    break;
                case 2:
                    try {
                        int empId = clientInputOutputCLI.fetchEmployeeId();
                        Employee employee = employeeController.getEmployeeById(empId);
                        clientInputOutputCLI.printEmployee(employee);
                    } catch (EmployeeFileReadException | ServerUnavilableException e) {
                        logger.error("Exception : ", e);
                    }
                    break;
                case 3:
                    try {
                        List<Employee> employeeList = employeeController.getAllEmployees();
                        clientInputOutputCLI.printEmployeeList(employeeList);
                    } catch (EmployeeFileReadException | ServerUnavilableException e) {
                        logger.error("Exception ; ", e);
                    }
                    break;
                case 4:
                    try {
                        int empId = clientInputOutputCLI.fetchEmployeeId();
                        if (employeeController.isEmployeeExists(empId)) {
                            Employee employee = clientInputOutputCLI.fetchEmployeeDetailsForUpdate();
                            employee.setEmpId(empId);
                            employeeController.updateEmployee(employee);
                        }
                    } catch (ServerUnavilableException | EmployeeNotFoundException | EmployeeFileUpdateException e) {
                        logger.error("Exception : ", e);
                    }
                    break;
                case 5:
                    try {
                        int empId = clientInputOutputCLI.fetchEmployeeId();
                        Employee employee = employeeController.deleteEmployeeById(empId);
                        System.out.println("Employee deleted successfully");
                        clientInputOutputCLI.printEmployee(employee);
                    } catch (EmployeeFileDeletionException | EmployeeFileUpdateException | EmployeeFileReadException |
                             EmployeeNotFoundException | ServerUnavilableException e) {
                        logger.info("Exception : ", e);
                    }
                    break;
                case 6:
                    try {
                        int empId = clientInputOutputCLI.fetchEmployeeId();
                        boolean isEmployeeExists = employeeController.isEmployeeExists(empId);
                        System.out.println("Is Employee Exists : " + isEmployeeExists);
                    } catch (ServerUnavilableException e) {
                        logger.error("Exception : ", e);
                    }
                    break;
                case 7:
                    int empId = clientInputOutputCLI.fetchEmployeeId();
                    Address address = employeeController.getAddressByEmployeeId(empId);
                    clientInputOutputCLI.printAddress(address);
                    break;
                case 8:
                    empId = clientInputOutputCLI.fetchEmployeeId();
                    address = employeeController.deleteAddressByEmployeeId(empId);
                    if (address != null) {
                        System.out.println("Address Deleted Successfully Address Deletion Failed");
                    }
                    break;
                case 9:
                    empId = clientInputOutputCLI.fetchEmployeeId();
                    address = clientInputOutputCLI.fetchAddressDetails();
                    address.setEmpId(empId);
                    address = employeeController.addAddressByEmployeeId(address);
                    clientInputOutputCLI.printAddress(address);
                    break;
                case 10:
                    empId = clientInputOutputCLI.fetchEmployeeId();
                    if (employeeController.getAddressByEmployeeId(empId) != null) {
                        address = clientInputOutputCLI.fetchAddressDetails();
                        address.setEmpId(empId);
                        address = employeeController.updateAddressByEmployeeId(address);
                        clientInputOutputCLI.printAddress(address);
                    }
                    break;
                case 11:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static Employee updateEmployee() throws EmployeeFileUpdateException, EmployeeNotFoundException {
        Employee employee = clientInputOutputCLI.fetchEmployeeDetailsForUpdate();
        return employee;
    }

    private static Employee addEmployee() throws ServerUnavilableException, EmployeeFileReadException {
        Employee employee = clientInputOutputCLI.fetchEmployeeDetails();
        employeeController.addEmployee(employee);
        return employee;
    }

    private static int fetchUserInput() {
        int userInput = -1;
        try {
            userInput = clientInputOutputCLI.fetchUserInput();
        } catch (InvalidUserInputException e) {
            logger.error("Exception : ", e);
        }
        return userInput;
    }

    /**
     * This method will take storageType input from the user,
     * convert it into a storageType enum,
     * and create an EmployeeController object while giving the storageType enum to the constructor.
     */
    private static void createEmployeeControllerInstance() {
        StorageType userStorageChoice = null;
        try {
            int storageType = clientInputOutputCLI.fetchStorageType();
            if (storageType == 1) {
                userStorageChoice = StorageType.IN_MEMORY;
            } else if (storageType == 2) {
                userStorageChoice = StorageType.IN_FILE;
            } else if (storageType == 3) {
                userStorageChoice = StorageType.IN_DB;
            }
            employeeController = new EmployeeController(userStorageChoice);
        } catch (InvalidUserInputException | FileCreationException | SQLException e) {
            logger.error("Exception : ", e);
        }
    }
}
