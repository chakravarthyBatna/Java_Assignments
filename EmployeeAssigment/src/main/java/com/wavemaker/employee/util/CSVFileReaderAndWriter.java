package com.wavemaker.employee.util;

import com.wavemaker.employee.exception.EmployeeFileReadException;
import com.wavemaker.employee.exception.EmployeeFileWriteException;
import com.wavemaker.employee.model.Employee;

import java.io.*;

public class CSVFileReaderAndWriter {
    private File file;
    public CSVFileReaderAndWriter(File file) {
        this.file = file;
    }
    public boolean writeEmployee(Employee employee) throws EmployeeFileWriteException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.file, true));
            String line = String.format("%d,%s,%d,%s,%s",
                    employee.getEmpId(),
                    employee.getName(),
                    employee.getAge(),
                    employee.getGender(),
                    employee.getAddress() != null ? employee.getAddress().toString() : "N/A");

            writer.write(line);
            writer.newLine();
            writer.flush();
            return true;
        } catch (IOException e) {
            throw new EmployeeFileWriteException("Error writing employee details to file", 500);
        } finally {
            closeBufferedWriter(writer);
        }
    }

    public boolean isEmployeeExists(int empId) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int existingEmpId = Integer.parseInt(fields[0]);
                if (existingEmpId == empId) {
                    closeBufferedReader(reader);
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new EmployeeFileReadException("Failed to read employee data from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return false;
    }

    private void closeBufferedReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Failed to Close the Reader," + e.getMessage());
            }
        }
    }

    private void closeBufferedWriter(BufferedWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Failed to close writer: " + e.getMessage());
            }
        }
    }
}
