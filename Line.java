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

import java.io.*;
import java.util.*;

/**
 * Utility class for CSVParser
 */
public class Line {
    public enum AccountType {
        CHECKING,
        SAVINGS
    }
    private final String[] line;

    /**
     * Create a Line object
     * @param line a row from data.csv
     */
    public Line(String[] line) {
        this.line = line;
    }

    /**
     * Create a Line object
     * @param id user id
     * @param fName first name
     * @param lName last name
     * @param pin pin
     */
    public Line(String id, String fName, String lName, String pin) {
        // opens an empty user
        this.line = new String[] {id, fName, lName, "false", "-1", "false", "-1", pin};
    }

    /**
     * Create a Line object
     * @param id user id
     * @param fName first name
     * @param lName last name
     * @param pin pin
     * @param accountType account type
     * @param accountValue account value
     */
    public Line(String id, String fName, String lName, String pin, AccountType accountType, double accountValue) {
        // opens an user with one account
        this.line = new String[] {
                id, fName, lName, accountType == AccountType.CHECKING ? "true" : "false",
                accountType == AccountType.CHECKING ? Double.toString(accountValue) : "-1",
                accountType == AccountType.SAVINGS ? "true" : "false",
                accountType == AccountType.SAVINGS ? Double.toString(accountValue) : "-1",
                pin};
    }

    /**
     * Create a Line object
     * @param id user id
     * @param fName first name
     * @param lName last name
     * @param pin pin
     * @param checkingValue account type
     * @param savingsValue account value
     */
    public Line(String id, String fName, String lName, String pin, double checkingValue, double savingsValue) {
        // opens an user with two accounts
        this.line = new String[] {
                id, fName, lName, "true", Double.toString(checkingValue), "true", Double.toString(savingsValue), pin};
    }


    /**
     * get the user id
     * @return user id
     */
    public String getId() {
        return line[0];
    }

    /**
     * get user first name
     * @return user first name
     */
    public String getfName() {
        return line[1];
    }

    /**
     * get user last name
     * @return user first name
     */
    public String getlName() {
        return line[2];
    }

    /**
     * check if user has a checking account open
     * @return if the user has a checking account
     */
    public boolean isChecking() {
        return Boolean.parseBoolean(line[3]);
    }

    /**
     * value of checking account
     * @return value of checking account
     */
    public double valueOfChecking() {
        return Double.parseDouble(line[4]);
    }

    /**
     * check if user has a savings account open
     * @return if the user has a savings account
     */
    public boolean isSavings() {
        return Boolean.parseBoolean(line[5]);
    }

    /**
     * savings account balance
     * @return the savings account balance
     */
    public double valueOfSavings() {
        return Double.parseDouble(line[6]);
    }

    /**
     * get the user's pin
     * @return the pin
     */
    public String getPin() {
        return line[7];
    }

    /**
     * change user id
     * @param id the new id
     */
    public void setId(String id) {
        line[0] = id;
    }

    /**
     * set first name
     * @param fName first name
     */
    public void setfName(String fName) {
        line[1] = fName;
    }

    /**
     * set last name
     * @param lName last name
     */
    public void setlName(String lName) {
        line[2] = lName;
    }

    /**
     * open or close a checking account
     * @param checking true to open account, and vice versa
     */
    public void setChecking(boolean checking) {
        line[3] = Boolean.toString(checking);
        // if the account is closed, set balance to -1
        if (!checking) {
            line[4] = "-1";
        }
    }

    /**
     * set checking account money value
     * @param valueOfChecking value of money
     */
    public void setValueOfChecking(double valueOfChecking) {
        line[4] = Double.toString(valueOfChecking);
    }

    /**
     * set savings account to be open or close
     * @param savings true to open account, and vice versa
     */
    public void setSavings(boolean savings) {
        line[5] = Boolean.toString(savings);
        // if the account is closed, set balance to -1
        if (!savings) {
            line[6] = "-1";
        }
    }

    /**
     * set monetary value of savings account
     * @param valueOfSavings money amount
     */
    public void setValueOfSavings(double valueOfSavings) {
        line[6] = Double.toString(valueOfSavings);
    }

    /**
     * change a pin to a new value
     * @param pin new pin value
     */
    public void setPin(String pin) {
        line[7] = pin;
    }

    /**
     * return a string array for use with CSVParser
     * @return a string array
     */
    public String[] getArray() {
        return line;
    }
}

