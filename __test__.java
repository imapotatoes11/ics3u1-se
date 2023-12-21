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
/*
 * Dec 20, 2023
 * The program
 * */
package se2;

import java.io.IOException;
import se2.UserError;
import se2.UserError.*;

public class __test__ {
    public static void main(String[] args) throws IOException {
//        ATM.createUser();
        try {
            System.out.println(ATM.validateLogin(123450, String.valueOf(1234)));
        } catch (UserError e) {
            switch (e.errorType) {
                case INVALID_PIN:
                    System.out.println("Invalid pin");
                    break;
                case INVALID_ID:
                    System.out.println("Invalid id");
                    break;
            }
        }
//        ATM.deposit(123457);

        CSVParser parser = new CSVParser("src/se2/data.csv");
//        System.out.println(parser.getLine(123457).getPin());
    }
}
