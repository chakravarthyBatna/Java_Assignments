package com.wavemaker.employee.util;

import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.model.Employee;

import java.util.Scanner;

public class EmployeeDataReaderUtil {

    public static Employee fetchEmployeeDetails(Scanner scanner, String operation) {
        Employee employee = new Employee();
        System.out.print("Enter Employee Id : ");
        int empId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter " + operation + " Employee Name : ");
        String name = scanner.nextLine();
        System.out.print("Enter " + operation + " Employee Gender : ");
        String gender = scanner.nextLine();
        System.out.print("Enter " + operation + " Employee Age : ");
        int age = scanner.nextInt();
        scanner.nextLine();
        employee.setEmpId(empId);
        employee.setName(name);
        employee.setAge(age);
        employee.setGender(gender);
        return employee;
    }
    public static Address fetchEmployeeAddress(Scanner scanner, String operation) {
        int userChoice;
        System.out.println("Do You Want To " + operation + " the Employee Address?\n1. For Yes and 2.For No");
        userChoice = scanner.nextInt();
        scanner.nextLine();
        if (userChoice == 1) {
            Address address = new Address();
            System.out.println("Enter Address Id : ");
            int addressId = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Enter Country :");
            String country = scanner.nextLine();
            System.out.println("Enter State : ");
            String state = scanner.nextLine();
            System.out.println("Enter Pincode : ");
            int pincode = scanner.nextInt();
            scanner.nextLine();
            address.setAddressId(addressId);
            address.setCountry(country);
            address.setState(state);
            address.setPincode(pincode);
            return address;
        }
        return null;
    }
}
