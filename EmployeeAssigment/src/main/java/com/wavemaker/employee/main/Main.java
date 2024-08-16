package com.wavemaker.employee.main;

import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;

import java.util.Scanner;

public class Main {
    private static EmployeeService employeeService;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        System.out.println("1. In Memory\n2.In File Storage");
        System.out.println("Enter Option to Choose the storage");
        int userChoice = scanner.nextInt();
        employeeService = new EmployeeServiceImpl(userChoice);

    }
}
