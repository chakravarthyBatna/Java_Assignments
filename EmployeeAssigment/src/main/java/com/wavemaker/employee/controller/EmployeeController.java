package com.wavemaker.employee.controller;

import com.wavemaker.employee.constant.Storage_Type;
import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.exception.InvalidUserInputException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.exception.employee.*;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.service.AddressService;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.AddressServiceImpl;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;
import com.wavemaker.employee.util.EmployeeDataReaderUtil;

import java.util.List;
import java.util.Scanner;

public class EmployeeController {
    private static EmployeeService employeeService;
    private static AddressService addressService;

    public static void main(String[] args) throws FileCreationException {
        Scanner scanner = new Scanner(System.in);
        Storage_Type userStorageChoice = null;
        boolean exit = false;
        int userChoice, empId;
        System.out.println("1. In Memory\n2.In File Storage");
        System.out.println("Enter Option to Choose the storage");
        userChoice = scanner.nextInt();
        if (userChoice == 1) {
            userStorageChoice = Storage_Type.IN_MEMORY;
        } else {
            userStorageChoice = Storage_Type.IN_FILE;
        }
        employeeService = new EmployeeServiceImpl(userStorageChoice);
        addressService = new AddressServiceImpl(userStorageChoice);
        while (!exit) {
            try {
                userChoice = fetchUserInput(scanner);
            } catch (InvalidUserInputException e) {
                System.out.println(e.getMessage());
                continue;
            }
            switch (userChoice) {
                case 1:
                    System.out.println("Enter Employee Details:");
                    try {
                        empId = addEmployee(scanner);
                        System.out.println("Your Generated Employee Id is : " + empId);
                    } catch (ServerUnavilableException | EmployeeFileReadException exception) {
                        System.out.println("Error While Adding the Employee" + exception.getMessage());
                    } catch (DuplicateEmployeeRecordFoundException e) {
                        System.out.println("Duplicate Record Found" + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.println("Enter Employee Id to Get Details:");
                    empId = scanner.nextInt();
                    try {
                        System.out.println(employeeService.getEmployeeById(empId));
                    } catch (EmployeeFileReadException e) {
                        System.out.println("Exception While reading the employee : " + e.getMessage());
                    }
                    break;
                case 3:
                    List<Employee> employees = null;
                    try {
                        employees = employeeService.getAllEmployeeDetails();
                    } catch (EmployeeFileReadException e) {
                        System.out.println("Exception while getting all the employees data ; " + e.getMessage());
                    }
                    System.out.println("Employee List:");
                    for (Employee emp : employees) {
                        System.out.println(emp);
                    }
                    break;
                case 4:
                    try {
                        updateEmployee(scanner);
                    } catch (EmployeeNotFoundException e) {
                        System.out.println("Employee Not Found to Update " + e.getMessage());
                    } catch (EmployeeFileUpdateException e) {
                        System.out.println("Error while updating the employee" + e.getMessage());
                    }
                    break;
                case 5:
                    System.out.println("Enter Employee ID to Delete:");
                    empId = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        Employee employee = employeeService.deleteEmployee(empId);
                        System.out.println("Employee Details: " + employee + "Deleted Successfully");
                    } catch (EmployeeNotFoundException | EmployeeFileReadException | EmployeeFileUpdateException |
                             EmployeeFileDeletionException exception) {
                        System.out.println("Error while deleting the employee " + exception.getMessage());
                    }
                    break;
                case 6:
                    System.out.println("Enter Employee Id to Check:");
                    empId = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        boolean isEmployeeExists = employeeService.isEmployeeExists(empId);
                        System.out.println("Is Employee Exists: " + isEmployeeExists);
                    } catch (ServerUnavilableException e) {
                        System.out.println("Exception while getting the employee details + " + e.getMessage());
                    }
                    break;
                case 7:
                    System.out.println("Enter Employee Id to Get Address:");
                    empId = scanner.nextInt();
                    Address address = addressService.getAddressByEmpId(empId);
                    System.out.println(address != null ? address : "No Address Found for Employee ID " + empId);
                    break;
                case 8:
                    System.out.println("Enter Employee ID to Delete Address:");
                    empId = scanner.nextInt();
                    Address isDeleted = addressService.deleteAddressByEmpId(empId);
                    if (isDeleted != null) {
                        System.out.println("Address Deleted Successfully Address Deletion Failed");
                    }
                    break;
                case 9:
                    System.out.println("Enter Employee ID to Add Address:");
                    empId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    try {
                        Employee employee = employeeService.getEmployeeById(empId);
                        if (employee != null) {
                            Address newAddress = EmployeeDataReaderUtil.fetchEmployeeAddress(scanner, "Add");
                            if (newAddress != null) {
                                newAddress.setEmpId(empId);
                                addressService.addAddress(newAddress);
                                System.out.println("Address Added Successfully");
                            }
                        }
                    } catch (EmployeeFileReadException e) {
                        System.out.println("Exception while adding address" + e.getMessage());
                    }
                    break;
                case 10:
                    System.out.println("Enter Employee ID to Update Address:");
                    empId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    Address updatedAddress = EmployeeDataReaderUtil.fetchEmployeeAddress(scanner, "Update");
                    updatedAddress.setEmpId(empId);
                    addressService.updateAddress(updatedAddress);
                    System.out.println("Address Updated Successfully");
                    break;
                case 11:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static Employee updateEmployee(Scanner scanner) throws EmployeeFileUpdateException, EmployeeNotFoundException {
        Employee employee = EmployeeDataReaderUtil.fetchEmployeeDetails(scanner, "New");
        Address address = addressService.getAddressByEmpId(employee.getEmpId());
        if (address != null) {
            address = EmployeeDataReaderUtil.fetchEmployeeAddress(scanner, "Update");
        }
        employee.setAddress(address);
        if (address != null) {
            address.setEmpId(employee.getEmpId());
        }
        employeeService.updateEmployee(employee);
        return employee;
    }

    private static int addEmployee(Scanner scanner) throws ServerUnavilableException, DuplicateEmployeeRecordFoundException, EmployeeFileReadException {
        Employee employee = EmployeeDataReaderUtil.fetchEmployeeDetails(scanner, "");
        employee.setAddress(EmployeeDataReaderUtil.fetchEmployeeAddress(scanner, "Add"));
        Address address = employee.getAddress();
        if (address != null) {
            address.setEmpId(employee.getEmpId());
        }
        return employeeService.addEmployee(employee);
    }

    private static int fetchUserInput(Scanner scanner) throws InvalidUserInputException {
        System.out.println("\nWelcome:");
        System.out.println("1. Add Employee");
        System.out.println("2. Get Employee by ID");
        System.out.println("3. Get All Employees");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("6. Know If Employee Exists or Not");
        System.out.println("7. Get Address by Employee ID");
        System.out.println("8. Delete Address by Employee ID");
        System.out.println("9. Add Address by Employee ID");
        System.out.println("10. Update Address by Employee ID");
        System.out.println("11. Exit");
        System.out.println("Enter your choice:");
        int userInput = scanner.nextInt();
        scanner.nextLine();
        if (userInput < 1 || userInput > 11) {
            throw new InvalidUserInputException("Invalid User Input Please enter the correct Input",400); //400 = bad request
        }
        return userInput;
    }
}
