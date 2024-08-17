package com.wavemaker.employee.controller;

import com.wavemaker.employee.constant.Storage_Type;
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
    public static void main(String[] args) {
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
            System.out.println("\nWelcome:");
            System.out.println("1. Add Employee");
            System.out.println("2. Get Employee by ID");
            System.out.println("3. Get All Employees");
            System.out.println("4. Update Employee");
            System.out.println("5. Delete Employee");
            System.out.println("6. Know That Employee Exists or Not");
            System.out.println("7. Exit");
            System.out.println("Enter your choice:");

            userChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (userChoice) {
                case 1:
                    System.out.println("Enter Employee Details:");
                    addEmployee(scanner);
                    break;
                case 2:
                    System.out.println("Enter Employee Id To get Details :");
                    empId = scanner.nextInt();
                    System.out.println(employeeService.getEmployeeById(empId));
                    break;
                case 3:
                    List<Employee> employees = employeeService.getAllEmployeeDetails();
                    System.out.println("Employee List:");
                    for (Employee emp : employees) {
                        System.out.println(emp);
                    }
                    break;
                case 4:
                    updateEmployee(scanner);
                    break;
                case 5:
                    System.out.println("Enter Employee ID to Delete:");
                    empId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Employee Details : " + employeeService.deleteEmployee(empId) + " Deleted Successfully");
                    break;
                case 6:
                    System.out.println("Enter Employee Id To Know ");
                    empId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Is Employee Exists : " + employeeService.isEmployeeExists(empId));
                    break;
                case 7:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static Employee updateEmployee(Scanner scanner) {
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

    private static boolean addEmployee(Scanner scanner) {
        Employee employee = EmployeeDataReaderUtil.fetchEmployeeDetails(scanner, "");
        employee.setAddress(EmployeeDataReaderUtil.fetchEmployeeAddress(scanner, "Add"));
        Address address = employee.getAddress();
        if (address != null) {
            address.setEmpId(employee.getEmpId());
        }
        return employeeService.addEmployee(employee);
    }
}
