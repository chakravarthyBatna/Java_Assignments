package com.wavemaker.employee.client;

import com.wavemaker.employee.exception.InvalidUserInputException;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.pojo.Employee;

import java.util.List;
import java.util.Scanner;

public class ClientInputOutputCLI {
    private static Scanner scanner;

    public ClientInputOutputCLI() {
        scanner = new Scanner(System.in);
    }

    public int fetchStorageType() throws InvalidUserInputException {
        int storageType;
        System.out.println("1. In Memory");
        System.out.println("2. In File Storage");
        System.out.println("3. In DB Storage");
        System.out.print("Enter an option to choose the storage: ");
        storageType = scanner.nextInt();
        if (storageType < 1 || storageType > 3) {
            throw new InvalidUserInputException("Invalid User Input", 400); // 400 = Bad Request
        }
        return storageType;
    }

    public int fetchUserInput() throws InvalidUserInputException {
        System.out.println("\nWelcome:");
        System.out.println("1. Add Employee");
        System.out.println("2. Get Employee by ID");
        System.out.println("3. Get All Employees");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("6. Check If Employee Exists");
        System.out.println("7. Get Address by Employee ID");
        System.out.println("8. Delete Address by Employee ID");
        System.out.println("9. Add Address by Employee ID");
        System.out.println("10. Update Address by Employee ID");
        System.out.println("11. Exit");
        System.out.print("Enter your choice: ");
        int userInput = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer
        if (userInput < 1 || userInput > 11) {
            throw new InvalidUserInputException("Invalid input. Please enter a number between 1 and 11.", 400);
        }

        return userInput;
    }

    public Employee fetchEmployeeDetailsForUpdate() {
        Employee employee = new Employee();
        System.out.print("Enter New Employee Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter New Employee Gender (MALE, FEMALE, OTHERS): ");
        String gender = scanner.nextLine();

        System.out.print("Enter New Employee Age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer

        System.out.print("Enter New Employee Email: ");
        String email = scanner.nextLine();

        employee.setName(name);
        employee.setGender(gender);
        employee.setAge(age);
        employee.setEmail(email);
        System.out.print("Do you want to update the address? (yes/no): ");
        String updateAddress = scanner.nextLine();

        if (updateAddress.equalsIgnoreCase("yes")) {
            Address address = new Address();

            System.out.print("Enter New State: ");
            String state = scanner.nextLine();

            System.out.print("Enter New Country: ");
            String country = scanner.nextLine();

            System.out.print("Enter New Pincode: ");
            int pincode = scanner.nextInt();
            scanner.nextLine(); // Clear the buffer

            address.setState(state);
            address.setCountry(country);
            address.setPincode(pincode);

            employee.setAddress(address);
        } else {
            employee.setAddress(null);
        }
        return employee;
    }

    public Employee fetchEmployeeDetails() {
        Employee employee = new Employee();

        System.out.print("Enter Employee Name: ");
        String name = scanner.nextLine();

        String gender = null;
        while (gender == null || !(gender.equalsIgnoreCase("MALE") || gender.equalsIgnoreCase("FEMALE") || gender.equalsIgnoreCase("OTHERS"))) {
            System.out.print("Enter Employee Gender (MALE, FEMALE, OTHERS): ");
            gender = scanner.nextLine();
            if (!(gender.equalsIgnoreCase("MALE") || gender.equalsIgnoreCase("FEMALE") || gender.equalsIgnoreCase("OTHERS"))) {
                System.out.println("Invalid gender entered. Please enter MALE, FEMALE, or OTHERS.");
            }
        }

        System.out.print("Enter Employee Age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer
        System.out.print("Enter Employee Email: ");
        String email = scanner.nextLine();
        int userInput = -1;
        System.out.println("Do you want to add Address Also?");
        System.out.print("1. for yes and 2. for no");
        userInput = scanner.nextInt();
        scanner.nextLine();
        if ((userInput == 1)) {
            Address address = fetchAddressDetails();
            employee.setAddress(address);
        }
        employee.setName(name);
        employee.setAge(age);
        employee.setGender(gender);
        employee.setEmail(email);
        return employee;
    }

    public Address fetchAddressDetails() {
        Address address = new Address();

        System.out.print("Enter State: ");
        String state = scanner.nextLine();
        System.out.print("Enter Country: ");
        String country = scanner.nextLine();
        System.out.print("Enter Pincode: ");
        int pincode = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer

        address.setState(state);
        address.setCountry(country);
        address.setPincode(pincode);

        return address;
    }

    public int fetchEmployeeId() {
        System.out.print("Enter Employee ID: ");
        int empId = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer
        return empId;
    }

    public void printEmployeeList(List<Employee> employeeList) {
        if (employeeList == null || employeeList.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        // Print headers for employees
        System.out.printf("%-10s %-20s %-10s %-5s %-30s", "EMP ID", "NAME", "GENDER", "AGE", "EMAIL");

        // Check if any employee has an address to print address headers
        boolean hasAddress = employeeList.stream().anyMatch(emp -> emp.getAddress() != null);
        if (hasAddress) {
            System.out.printf(" %-10s %-20s %-20s %-10s%n", "ADDRESS ID", "COUNTRY", "STATE", "PINCODE");
        } else {
            System.out.println();
        }

        System.out.println("===================================================================================================================");

        // Print employee details
        for (Employee employee : employeeList) {
            System.out.printf("%-10d %-20s %-10s %-5d %-30s",
                    employee.getEmpId(), employee.getName(), employee.getGender(),
                    employee.getAge(), employee.getEmail());

            if (employee.getAddress() != null) {
                Address address = employee.getAddress();
                // Adjusted format specifiers to ensure alignment
                System.out.printf(" %-10d %-20s %-20s %-10d%n",
                        address.getAddressId(), address.getCountry(), address.getState(), address.getPincode());
            } else {
                System.out.println();
            }
        }
    }



    public void printEmployee(Employee employee) {
        System.out.printf("%-10s %-20s %-10s %-5s %-30s", "EMP ID", "NAME", "GENDER", "AGE", "EMAIL");
        if (employee.getAddress() != null) {
            System.out.printf(" %-10s %-20s %-20s %-10s%n", "ADDRESS ID", "COUNTRY", "STATE", "PINCODE");
        } else {
            System.out.println();
        }

        System.out.println("===================================================================================================================");

        System.out.printf("%-10d %-20s %-10s %-5d %-30s",
                employee.getEmpId(), employee.getName(), employee.getGender(),
                employee.getAge(), employee.getEmail());
        if (employee.getAddress() != null) {
            Address address = employee.getAddress();
            System.out.printf(" %-10d %-20s %-20s %-10d%n",
                    address.getAddressId(), address.getCountry(), address.getState(), address.getPincode());
        } else {
            System.out.println();
        }
    }

    public void printAddress(Address address) {
        if (address == null) {
            System.out.println("No address details available.");
            return;
        }

        System.out.printf("%-10s %-20s %-20s %-10s%n", "ADDRESS ID", "COUNTRY", "STATE", "PINCODE");
        System.out.println("===================================================================================");

        System.out.printf("%-10d %-20s %-20s %-10d%n",
                address.getAddressId(), address.getCountry(), address.getState(), address.getPincode());
    }
}

