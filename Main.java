/*
 * Copyright 2023 Kevin Wang
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
// package se2;

import java.io.*;
import java.util.*;

public class Main {
    public static void loop() throws UserError, IOException {
        // initialize objects and classes

        // all methods are static so no need
        // to create an ATM object
        Scanner sc = new Scanner(System.in);
        CSVParser parser = new CSVParser(ATM.CSV_DIRECTORY);
        int ID; String pin; // used for login validation


        // login
        System.out.println("Please provide your login information");
        System.out.print("ID: ");
        ID = sc.nextInt();

        // check if the ID is valid
        try {
            parser.getLine(ID);
        } catch (IOException e) {
            System.out.printf("Invalid ID: %d\n", ID);

            // ask if the user wants to create a new account
            System.out.print("Would you like to create a new account? (y/n) ");
            String ans = sc.next();
            if (ans.equals("y")) {
                // create a new account
                ATM.createUser();
            }
            return;
        }

        // ask for pin
        System.out.print("PIN: ");
        pin = sc.next();

        // validate login
        try {
            if (ATM.validateLogin(ID, pin)) {
                System.out.println("Login successful");
                System.out.println("Welcome " + parser.getLine(ID).getfName() + " " + parser.getLine(ID).getlName());

                for (;;) {
                    // print a new line to clean up
                    System.out.println();

                    // the login was successful, ask if the user wants to
                    // deposit, withdraw, open an account, close an account,
                    // change their pin, or quit
                    System.out.println("What would you like to do?");
                    System.out.println("1. Deposit");
                    System.out.println("2. Withdraw");
                    System.out.println("3. Open an account");
                    System.out.println("4. Close an account");
                    System.out.println("5. Change your PIN");
                    System.out.println("6. Quit");
                    System.out.print("Enter a number: ");
                    int ans = sc.nextInt();

                    // handle the user's choice
                    switch (ans) {
                        case 1:
                            // deposit
                            ATM.deposit(ID);
                            break;
                        case 2:
                            // withdraw
                            ATM.withdraw(ID);
                            break;
                        case 3:
                            // open an account
                            ATM.createAccount(ID);
                            break;
                        case 4:
                            // close an account
                            ATM.closeAccount(ID);
                            break;
                        case 5:
                            // change pin
                            ATM.changePIN(ID);
                            break;
                        case 6:
                            // quit
                            return;
                        default:
                            // invalid choice
                            System.out.println("Invalid choice");
                            break;
                    }
                }
            } else {
                System.out.println("Login failed, the PIN was invalid");
            }
        } catch (UserError e) {
//            if (e.errorType == UserError.ErrorType.INVALID_ID) {
//                System.out.printf("Invalid ID: %d\n", ID);
//
//                // ask if the user wants to create a new account
//                System.out.print("Would you like to create a new account? (y/n) ");
//                String ans = sc.next();
//                if (ans.equals("y")) {
//                    // create a new account
//                    ATM.createUser();
//                }
//            }
        }
    }

    public static void main(String[] args) {
        while (true) {
            try {
                Main.loop();
            } catch (UserError e) {
                switch (e.errorType) {
                    case GENERIC -> System.out.println("An unexpected error occurred");
                    case INVALID_ID -> System.out.println("A problem occured: the ID was invalid");
                    case INVALID_PIN -> System.out.println("A problem occured: the PIN was invalid");
                }
            } catch (IOException e) {
                System.out.println("A problem occurred: " + e.getMessage());
            }
        }
    }
}
