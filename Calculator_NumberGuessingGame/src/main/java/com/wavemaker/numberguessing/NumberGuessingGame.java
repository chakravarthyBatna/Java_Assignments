package com.wavemaker.numberguessing;

import java.util.Scanner;

public class NumberGuessingGame {
    public static void main(String[] args) {
        NumberGuessingGame.guessNumber(); //start the game;
    }

    public static void guessNumber() {
        int noOfAttempts = 0, guess = 0;
        int secretNumber = 1 + (int) (100 * Math.random());
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Chakravarthy Number Guessing game");
        System.out.println("Your task is to guess a Number between 1 to 100 within n attempts");
        System.out.println("Where n is the no of attempts you can select\n Lets Go ;");

        System.out.println("Please Select the No of Attempts you want to try");
        noOfAttempts = scanner.nextInt();
        System.out.println("You Have Selected :" + noOfAttempts + " Attempts");
        System.out.println("Now guess the the Number Within :" + noOfAttempts + " Attempts");

        for (int i = 0; i <= noOfAttempts; i++) {
            if (guess == -1) {  //exit the program
                System.exit(0);
            }
            if (i == noOfAttempts) {
                System.out.println("You have exhausted all " + noOfAttempts + " attempts.");
                System.out.println("The secret number was " + secretNumber);
            }
            System.out.println("Attempt :" + (i + 1) + " Enter your Guess");
            guess = scanner.nextInt();
            if (guess == secretNumber) {
                System.out.println("Congratulations! You've guessed the correct number.");
                break;
            } else if (guess > secretNumber) {
                System.out.println("The secret number is Less than your guess.");
            } else {
                System.out.println("The secret number is Greater than your guess.");
            }
        }
    }
}
