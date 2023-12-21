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
 * Dec. 13, 2023
 * The program
 * */
package se2;

import java.io.*;
import java.util.ArrayList;

/**
 * Simple CSV Parser class
 */
public class CSVParser {
    String file;
    ArrayList<Line> lines = new ArrayList<>();

    /**
     * Create a new CSVParser object
     * @param fileName relative or absolute path to the csv file + file name
     * @throws IOException
     */
    public CSVParser(String fileName) throws IOException {
        this.file = fileName;
        this.update();
    }

    /**
     * Update (read) the data from file to the class
     * @throws IOException
     */
    public void update() throws IOException {
        // read data.csv and store to this.lines
        BufferedReader br = new BufferedReader(new FileReader(this.file));
        String line;
        while ((line = br.readLine()) != null) {
            this.lines.add(new Line(line.split(",")));
        }
    }

    /**
     * return a line given an id
     * @param id id of user (6 digits)
     * @return Line object with user details
     * @throws IOException
     */
    public Line getLine(int id) throws IOException {
        // iterate through all of this.lines and find and return the line with the id
        // (the first string is the id)
        for (Line line : this.lines) {
            if (line.getArray()[0].equals(Integer.toString(id))) {
                return line;
            }
        }
        throw new IOException("No such id was found");
    }

    /**
     * add a line (user) to the csv file
     * @param line
     * @throws IOException
     */
    public void addLine(Line line) throws IOException {
        // add a line to this.lines and return the line
        this.lines.add(line);
    }

    /**
     * remove a user
     * @param id user id
     * @return the user that was removed
     * @throws IOException
     */
    public Line removeLine(int id) throws IOException {
        // iterate through all of this.lines and find and remove the line with the id
        // (the first string is the id)
        for (int i = 0; i < this.lines.size(); i++) {
            if (this.lines.get(i).getArray()[0].equals(Integer.toString(id))) {
                return this.lines.remove(i);
            }
        }
        throw new IOException("No such id was found");
    }

    /**
     * write the data to the csv file
     * @throws IOException
     */
    public void write() throws IOException {
        // write this.lines to data.csv
        BufferedWriter bw = new BufferedWriter(new FileWriter(this.file));
        for (Line line : this.lines) {
            bw.write(String.join(",", line.getArray()));
            bw.newLine();
        }
        bw.close();
    }
}