package com.wavemaker.calculator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        calculator.calculate();
    }

    public void calculate() {
        Calculate calculate = new Calculate();
        BigDecimal numOne = null, numTwo = null, answer = null;
        int choice;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Chakravarthy Casio Calculator");
        while (true) {
            System.out.println("Please select the operation you want to perform:");
            System.out.println("1 for Addition (+)");
            System.out.println("2 for Subtraction (-)");
            System.out.println("3 for Multiplication (*)");
            System.out.println("4 for Division (/)");
            choice = scanner.nextInt();

            System.out.println("Enter First Number : ");
            numOne = scanner.nextBigDecimal();
            System.out.println("Enter Second Number : ");
            numTwo = scanner.nextBigDecimal();

            switch (choice) {
                case 0 : System.exit(0);
                case 1:
                    answer = calculate.addNum(numOne, numTwo);
                    System.out.println("Result: " + answer);
                    break;
                case 2:
                    answer = calculate.subNum(numOne, numTwo);
                    System.out.println("Result: " + answer);
                    break;
                case 3:
                    answer = calculate.mulNum(numOne, numTwo);
                    System.out.println("Result: " + answer);
                    break;
                case 4:
                    try {
                        answer = calculate.divNum(numOne, numTwo);
                        System.out.println("Result: " + answer);
                    } catch (ArithmeticException e) {
                        System.out.println("Error: Division by zero is not allowed.");
                        return;
                    }
                    break;
                default:
                    System.out.println("Invalid selection! Please choose a valid operation.");
            }

        }
    }

}
