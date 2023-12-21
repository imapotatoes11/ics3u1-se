/*
 * Copyright 2023 Kevin Wang, Max Chu, Aryan Dhankhar
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the license at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the license is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * license for the specific language governing permissions and limitations under
 * the license.
 * */
/*
 * Dec. 15, 2023
 * Logic engine for the ICS3U Software Engineering project
 * */
// package se2;

// import java.io.IOException;
// import java.util.Objects;
// import java.util.Scanner;
// import se2.Line.*;
import java.io.*;
import java.util.*;

public class ATM {
    public static final String CSV_DIRECTORY = "src/se2/data.csv";

    /**
     * Validate user login. Will throw corresponding error if user id is not found
     * @param userID the user id number (6 digit unsigned integer)
     * @param pin the user's 4 digit pin (string)
     * @return true if all information is correct, false if the pin is incorrect
     * @throws UserError will throw an error if the pin or user id is incorrect
     */
    public static boolean validateLogin(int userID, String pin) throws UserError {
        // create the return object
        boolean booleanValue = false;
        // the correct pin file
        String pinFile;
        // try to look for ID in data.csv
        try{
            // create a new CSVParser object
            CSVParser parser = new CSVParser(CSV_DIRECTORY);
            // grab pin from file
            pinFile = parser.getLine(userID).getPin();

            // verify the pin
            if (!Objects.equals(pin, pinFile)){
                // the pin was invalid, return false
                return false;
            } else{
                booleanValue = true;
            }
        } catch(IOException e){
            // catch the IOException & assume the error was due to
            // an invalid ID

            // throw the corresponding error
            throw new UserError("User ID not found", UserError.ErrorType.INVALID_ID);
        }
        // return the return object
        return booleanValue;
    }

    /**
     * Create a new user. the method will ask user for all required information
     * via standard in/out. The information required is as follows: <br>
     * - User ID (6 digit unsigned integer, cannot be duplicate in database) <br>
     * - PIN (4 digit unsigned integer) <br>
     * - First name (string) <br>
     * - Last name (string) <br>
     */
    public static void createUser() throws UserError {
        // Initialize objects
        Scanner sc = new Scanner(System.in);
        String fname, lname;
        String userID = "", pin = "";
        boolean validInput = false;

        // ask for user ID, if invalid, keep asking
        do {
            System.out.print("Enter a 6 digit positive number as your user ID (must be unique): ");
            try {
                userID = sc.nextLine();

                // validate user ID
                if (Integer.parseInt(userID) <= 0) {
                    // the user id was not a positive number
                    System.out.println("Please enter a positive number.");
                } else if (userID.length() != 6) {
                    // the user id was not 6 digits
                    System.out.println("Please enter a 6-digit number.");
                } else {
                    // check if the user ID is unique
                    boolean isUnique = true;
                    try {
                        CSVParser parser = new CSVParser(CSV_DIRECTORY);
                        for (Line line : parser.lines) {
                            if (line.getId().equals(userID)) {
                                // the user ID is not unique
                                System.out.println("User ID already exists.");
                            }
                        }
                    } catch (IOException ignored) {}
                    validInput = isUnique;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        } while (!validInput);


        // ask for PIN, if invalid, keep asking
        validInput = false;
        do {
            System.out.print("Enter a 4 digit positive number as your pin: ");
            try {
                pin = sc.nextLine();
                // validate pin
                if (Integer.parseInt(pin) <= 0) {
                    // the pin was not a positive number
                    System.out.println("Please enter a positive number.");
                } else if (pin.length() != 4) {
                    // the pin was not 4 digits
                    System.out.println("Please enter a 4-digit number.");
                } else {
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                // catch the NumberFormatException & assume the error was due to
                // an invalid PIN

                // throw the corresponding error
                throw new UserError("Invalid PIN", UserError.ErrorType.INVALID_PIN);
            }
        } while (!validInput);


        // ask for first and last name
        // and convert to uppercase
        System.out.print("Enter your first name: ");
        fname = sc.nextLine().toUpperCase();
        System.out.print("Enter your last name: ");
        lname = sc.nextLine().toUpperCase();

        // try to write data to file under a new entry
        // with all accounts closed by default
        try {
            // create a new CSVParser object using the CSV_DIRECTORY constant
            CSVParser parser = new CSVParser(CSV_DIRECTORY);

            // add a new line (user) to the CSVParser object
            // the accounts are defaulted to closed
            parser.addLine(new Line(userID, fname, lname, pin));

            // make sure to write the updated data to file
            parser.write();

            // confirm with user the creation of the user
            System.out.println("The user was successfully created.");
        } catch (IOException e) {
            // catch the IOException
            // not sure what the exception is
            // so just make it generic
            throw new UserError("An unexpected error occured", UserError.ErrorType.GENERIC);
        }
    }

    /**
     * Create a new account for the user. The method will ask the user which account
     * they want to open if they have no accounts, and will automatically open the
     * other account if they only have one. The method will print an error message
     * if the user has both accounts.
     * @param userID the user id number (6 digit unsigned integer)
     */
    public static void createAccount(int userID) throws UserError {
        try {
            // initialize objects
            CSVParser parser = new CSVParser(CSV_DIRECTORY);
            Scanner sc = new Scanner (System.in);
            boolean hasChecking = parser.getLine(userID).isChecking();
            boolean hasSavings = parser.getLine(userID).isSavings();
            Line.AccountType accountToOpen;

            // check which accounts the user has open
            if (hasChecking && hasSavings) {
                // can't do anything if the user has both accounts open
                System.out.println("You already have both accounts.");
            } else if (hasChecking) {
                // open a savings account and set balance to 0
                parser.getLine(userID).setSavings(true);
                parser.getLine(userID).setValueOfSavings(0);

                // print confirmation message
                System.out.println("You have opened a savings account (your balance is $0).");

                // save changes to file
                parser.write();
            } else if (hasSavings) {
                // open a checking account and set balance to 0
                parser.getLine(userID).setChecking(true);
                parser.getLine(userID).setValueOfChecking(0);

                // print confirmation message
                System.out.println("You have opened a checking account (your balance is $0).");

                // save changes to file
                parser.write();
            } else {
                // ask user which account to open
                System.out.print("Which kind of account do you want to open, c for checking, and s for savings: ");
                accountToOpen = Objects.equals(sc.nextLine(), "c") ? Line.AccountType.CHECKING : Line.AccountType.SAVINGS;

                if (accountToOpen == Line.AccountType.CHECKING) {
                    // open a checking account
                    parser.getLine(userID).setChecking(true);
                    parser.getLine(userID).setValueOfChecking(0);

                    // print confirmation message
                    System.out.println("You have opened a checking account (your balance is $0).");
                } else {
                    // open a savings account
                    parser.getLine(userID).setSavings(true);
                    parser.getLine(userID).setValueOfSavings(0);

                    // print confirmation message
                    System.out.println("You have opened a savings account (your balance is $0).");
                }

                // save changes to file
                parser.write();
            }

        } catch (IOException e) {
            // catch the IOException
            // not sure what the exception is
            // so just make it generic
            throw new UserError("An unexpected error occured", UserError.ErrorType.GENERIC);
        }
    }

    /**
     * Close an account for the user. The method will ask the user which account
     * they want to close if they have both accounts, and will automatically close
     * the other account if they only have one. The method will print an error message
     * if the user has no accounts.
     * @param userID the user id number (6 digit unsigned integer)
     */
    public static void closeAccount(int userID) throws UserError {
        try {
            // initialize objects
            CSVParser parser = new CSVParser(CSV_DIRECTORY);
            Scanner sc = new Scanner (System.in);
            boolean hasChecking = parser.getLine(userID).isChecking();
            boolean hasSavings = parser.getLine(userID).isSavings();
            Line.AccountType accountType;

            // check which accounts the user has open
            if ((!hasChecking) && !hasSavings) {
                // can't do anything if the user has no accounts open
                System.out.println("You do not have any accounts open.");
            } else if (hasChecking && !hasSavings) {
                // close checking account
                parser.getLine(userID).setChecking(false);
                parser.getLine(userID).setValueOfChecking(-1);

                // print confirmation message
                System.out.println("You have closed your checking account.");

                // save changes to file
                parser.write();
            } else if (hasSavings && !hasChecking) {
                // close savings account
                parser.getLine(userID).setSavings(false);
                parser.getLine(userID).setValueOfSavings(-1);

                // print confirmation message
                System.out.println("You have closed your savings account.");

                // save changes to file
                parser.write();
            } else {
                // ask which account to close
                System.out.print("Do you want to close your checking or savings account (c for checking, s for savings)? ");
                accountType = Objects.equals(sc.nextLine(), "c") ? Line.AccountType.CHECKING : Line.AccountType.SAVINGS;

                // close the corresponding account
                if (accountType == Line.AccountType.CHECKING) {
                    // close the checking account
                    parser.getLine(userID).setChecking(false);
                    parser.getLine(userID).setValueOfChecking(-1);

                    // print confirmation message
                    System.out.println("You have closed your checking account.");
                } else {
                    // close the savings account
                    parser.getLine(userID).setSavings(false);
                    parser.getLine(userID).setValueOfSavings(-1);

                    // print confirmation message
                    System.out.println("You have closed your savings account");
                }

                // save changes to file
                parser.write();
            }

        } catch (IOException e) {
            // catch the IOException
            // not sure what the exception is
            // so just make it generic
            throw new UserError("An unexpected error occured", UserError.ErrorType.GENERIC);
        }
    }

    /**
     * Deposit money into an account. The method will ask the user which account
     * they want to deposit to if they have both accounts, and will automatically
     * deposit to the other account if they only have one. The method will print an
     * error message if the user has no accounts.
     * @param userID the user id number (6 digit unsigned integer)
     */
    public static void deposit(int userID) throws UserError {
        // initialize objects
        Scanner sc = new Scanner(System.in);
        int answered = 0; // flag to track if the user entered a valid input
        // initialize account type to prevent problems
        Line.Line.AccountType accountType = Line.Line.AccountType.CHECKING; // account type
        String newAccount; // whether the user wants to make a new account
        int accounts = 0; // number of accounts
        double depositAmount; // amount to deposit
        double originalBalance = 0; // original balance
        boolean accountChecking; // whether the user has a checking account
        boolean accountSavings; // whether the user has a savings account

        // try to do the shenanigans
        try {
            // create a new CSVParser object
            CSVParser parser = new CSVParser(CSV_DIRECTORY);

            // check how many accounts the user has
            accountChecking = parser.getLine(userID).isChecking();
            accountSavings = parser.getLine(userID).isSavings();
            if (accountChecking){
                accounts++;
            }
            if (accountSavings){
                accounts++;
            }
            // switch case the account stuff
            switch(accounts){
                // if they have no accounts print and error and ask for if they want to make one
                case 0:
                    System.out.println("Error, you have no accounts!");
                    while(answered == 0) {
                        // prompts user to ask if they want a new account
                        System.out.print("Do you want to make an account?(y/n) ");
                        newAccount = sc.nextLine();
                        // checks whether they do want a new account
                        if (newAccount.equals("y")) {
                            // create a new account via createAccount()
                            createAccount(userID);
                            answered++;
                        }
                        else if (newAccount.equals("n")){
                            answered++;
                        }
                        else{
                            // the input was not valid
                            System.out.println("Please enter a valid input\n");
                        }
                    }
                    break;

                // if they have one account it checks which one
                case 1:
                    if (accountChecking){
                        accountType = Line.Line.AccountType.CHECKING;
                        originalBalance = parser.getLine(userID).valueOfChecking();
                    }
                    else {
                        accountType = Line.Line.AccountType.SAVINGS;
                        originalBalance = parser.getLine(userID).valueOfSavings();
                    }
                    break;

                // if they have 2 we want to know which account they want to deposit to
                case 2:
                    while(answered == 0) {
                        // prompts user which account they want to deposit too
                        System.out.print("Which account do you want to deposit to (c for checking, s for savings):");
                        accountType = Objects.equals(sc.nextLine(), "c") ? Line.Line.AccountType.CHECKING : Line.Line.AccountType.SAVINGS;
                        if (accountType == Line.Line.AccountType.SAVINGS) {
                            originalBalance = parser.getLine(userID).valueOfSavings();
                            answered++;
                        } else {
                            originalBalance = parser.getLine(userID).valueOfChecking();
                            answered++;
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + accounts);
            }
            answered = 0;
            while(answered == 0) {
                // asks the user how much they want to deposit
                System.out.print("Deposit: $");
                depositAmount = sc.nextDouble();
                if (depositAmount > 0.00) {
                    // for each account type:
                    // add the deposit amount to the original balance
                    // set the new balance to the new value
                    // print the new balance
                    if (accountType == Line.Line.AccountType.SAVINGS){
                        parser.getLine(userID).setValueOfSavings(round(originalBalance+depositAmount));
                        parser.write();
                        System.out.printf("Your savings accounts balance is now $%.2f\n", (parser.getLine(userID).valueOfSavings()));//(originalBalance+depositAmount));
                    } else {
                        parser.getLine(userID).setValueOfChecking(round(originalBalance+depositAmount));
                        parser.write();
                        System.out.printf("Your checking accounts balance is now $%.2f\n", (parser.getLine(userID).valueOfChecking()));//(originalBalance+depositAmount));
                    }
                    answered++;
                }
            }

        } catch(IOException e){
            // catch the IOException
            // not sure what the exception is
            // so just make it generic
            throw new UserError("An unexpected error occured", UserError.ErrorType.GENERIC);
        }
    }

    /**
     * Withdraw money from an account. The method will ask the user which account
     * they want to withdraw from if they have both accounts, and will automatically
     * withdraw from the other account if they only have one. The method will print an
     * error message if the user has no accounts or if they try to withdraw more money
     * than they have.
     * @param userID the user id number (6 digit unsigned integer)
     */
    public static void withdraw(int userID) throws UserError{
        // initialize objects
        Scanner sc = new Scanner(System.in);
        int answered = 0; // flag to track if the user entered a valid input
        // initialize account type to prevent problems
        Line.AccountType accountType = Line.Line.AccountType.CHECKING; // account type
        String newAccount; // whether the user wants to make a new account
        int accounts = 0; // number of accounts
        double withdrawAmount; // amount to withdraw
        double originalBalance = 0; // original balance
        boolean accountChecking; // whether the user has a checking account
        boolean accountSavings; // whether the user has a savings account

        // try and do the shenanigans
        try {
            // create a new CSVParser object
            CSVParser parser = new CSVParser(CSV_DIRECTORY);

            // check how many accounts the user has
            accountChecking = parser.getLine(userID).isChecking();
            accountSavings = parser.getLine(userID).isSavings();
            if (accountChecking){
                accounts++;
            }
            if (accountSavings){
                accounts++;
            }
            // switch case for what to do for each amount of accounts
            switch(accounts){
                // if they have no accounts print and error and ask for if they want to make one
                case 0:
                    System.out.println("Error, you have no accounts!");
                    while(answered == 0) {
                        // prompts user to ask if they want a new account
                        System.out.print("Do you want to make an account?(y/n) ");
                        newAccount = sc.nextLine();
                        // checks whether they do want a new account
                        if (newAccount.equals("y")) {
                            createAccount(userID);
                            answered++;
                        }
                        else if (newAccount.equals("n")){
                            answered++;
                        }
                        else{
                            // the input wasn't valid
                            System.out.println("Please enter a valid input\n");
                        }
                    }
                    break;

                // if they have one account it checks which one
                case 1:
                    if (accountChecking){
                        accountType = Line.AccountType.CHECKING;
                        originalBalance = parser.getLine(userID).valueOfChecking();
                    }
                    else {
                        accountType = Line.AccountType.SAVINGS;
                        originalBalance = parser.getLine(userID).valueOfSavings();
                    }
                    break;

                // if they have 2 we want to know which account they want to deposit to
                case 2:
                    while(answered == 0) {
                        // prompts user which account they want to deposit too
                        System.out.print("Which account do you want to withdraw from:");
                        accountType = Objects.equals(sc.nextLine(), "c") ? Line.Line.AccountType.CHECKING : Line.Line.AccountType.SAVINGS;
                        if (accountType == Line.AccountType.SAVINGS) {
                            originalBalance = parser.getLine(userID).valueOfSavings();
                            answered++;
                        } else {
                            originalBalance = parser.getLine(userID).valueOfChecking();
                            answered++;
                        }
                    }
                    break;

            }
            answered = 0;
            while(answered == 0) {
                // prompts the user for the amount they want to withdraw
                System.out.print("Withdraw: $");
                withdrawAmount = sc.nextDouble();
                if (withdrawAmount > 0.00) {
                    if (accountType == Line.AccountType.SAVINGS){
                        if (originalBalance > withdrawAmount) {
                            parser.getLine(userID).setValueOfSavings((originalBalance - withdrawAmount));
                            System.out.printf("Your savings accounts balance is now $%.2f", (originalBalance - withdrawAmount));
                        }
                        else{
                            System.out.println("Error, cannot withdraw that much.");
                            answered--;
                        }
                    } else {
                        if (originalBalance > withdrawAmount) {
                            parser.getLine(userID).setValueOfChecking((originalBalance - withdrawAmount));
                            System.out.printf("Your checking accounts balance is now $%.2f", (originalBalance - withdrawAmount));
                        }
                        else{
                            System.out.println("Error, cannot withdraw that much.");
                            answered--;
                        }
                    }
                    answered++;
                }
            }

        }catch(IOException e){
            // the id was not found
            throw new UserError("User ID not found", UserError.ErrorType.INVALID_ID);
        }
    }

    public static void changePIN(int userID) throws UserError {
        // initialize objects
        Scanner sc = new Scanner(System.in);
        String newPIN;
        String newPIN2;
        boolean validInput = false;

        // ask for new PIN, if invalid, keep asking
        do {
            System.out.println("Enter a 4 digit positive number as your new pin: ");
            try {
                newPIN = sc.nextLine();
                // validate pin
                if (Integer.parseInt(newPIN) <= 0) {
                    // the pin was not a positive number
                    System.out.println("Please enter a positive number.");
                } else if (newPIN.length() != 4) {
                    // the pin was not 4 digits
                    System.out.println("Please enter a 4-digit number.");
                } else {
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                // catch the NumberFormatException & assume the error was due to
                // an invalid PIN

                // throw the corresponding error
                throw new UserError("Invalid PIN", UserError.ErrorType.INVALID_PIN);
            }
        } while (!validInput);

        // ask for new PIN again, if invalid, keep asking
//        do {
            System.out.println("Enter your new pin again: ");
            try {
                newPIN2 = sc.nextLine();
                // validate pin
                if (Integer.parseInt(newPIN2) <= 0) {
                    // the pin was not a positive number
                    System.out.println("Please enter a positive number.");
                } else if (newPIN2.length() != 4) {
                    // the pin was not 4 digits
                    System.out.println("Please enter a 4-digit number.");
                } else {
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                // catch the NumberFormatException & assume the error was due to
                // an invalid PIN

                // throw the corresponding error
                throw new UserError("Invalid PIN", UserError.ErrorType.INVALID_PIN);
            }
//        } while (!validInput);

        // check if the two PINs are the same
        if (newPIN.equals(newPIN2)) {
            // the two PINs are the same, change the PIN
            try {
                // create a new CSVParser object using the CSV_DIRECTORY constant
                CSVParser parser = new CSVParser(CSV_DIRECTORY);

                // change the PIN
                parser.getLine(userID).setPin(newPIN);

                // make sure to write the updated data to file
                parser.write();

                // confirm with user the creation of the user
                System.out.println("The PIN was successfully changed.");
            } catch (IOException e) {
                // catch the IOException
                // not sure what the exception is
                // so just make it generic
                throw new UserError("An unexpected error occured", UserError.ErrorType.GENERIC);
            }
        } else {
            // the two PINs are not the same, throw an error
            throw new UserError("The two PINs are not the same");
        }
    }


    private static double round(double value) {
        // round to 2 decimal places
        return Math.round(value * 100.0) / 100.0;
    }
}


/**
 * UserError class. This class is used to throw errors when the user inputs invalid
 * information. The class contains an error type enum which is used to determine
 * what type of error occurred.
 */
class UserError extends Exception{
    public ErrorType errorType = null;
    public enum ErrorType {
        GENERIC,
        INVALID_ID,
        INVALID_PIN
    }
    public UserError(String errorMessage){
        super(errorMessage);

        this.errorType = ErrorType.GENERIC;
    }
    public UserError(String errorMessage, ErrorType errorType){
        super(UserError.getErrorMessage(errorType) + ": " + errorMessage);

        this.errorType = errorType;
    }
    private static String getErrorMessage(ErrorType errorType){
        return switch (errorType) {
            case GENERIC -> "An error occurred";
            case INVALID_ID -> "The specified user was not found";
            case INVALID_PIN -> "The specified PIN was invalid";
            default -> "Unknown error";
        };
    }
}
