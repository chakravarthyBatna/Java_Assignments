package com.wavemaker.employee.util;

import com.wavemaker.employee.pojo.Employee;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeCSVFileReaderAndWriter {
    private final File file;
    private static int maxEmployeeId = 0;

    public EmployeeCSVFileReaderAndWriter(File file) {
        this.file = file;
    }

    public int getMaxEmployeeId() throws EmployeeFileReadException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                maxEmployeeId = Math.max(maxEmployeeId, id);
            }
        } catch (IOException e) {
            throw new EmployeeFileReadException("Unable to read the employee file", 500);
        }
        return maxEmployeeId;
    }

    public Employee readEmployeeByEmpId(int empId) throws EmployeeFileReadException {
        BufferedReader reader = null;
        Employee employee = null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 4) {
                    continue; // Skip any malformed lines
                }
                int currentEmpId = Integer.parseInt(fields[0]);
                if (currentEmpId == empId) {
                    String name = fields[1];
                    int age = Integer.parseInt(fields[2]);
                    String gender = fields[3];

                    employee = new Employee();
                    employee.setEmpId(empId);
                    employee.setName(name);
                    employee.setAge(age);
                    employee.setGender(gender);

                    return employee;
                }
            }
//will implement this line later
//            throw new EmployeeNotFoundException("Employee with ID: " + empId + " not found", 404);

        } catch (IOException e) {
            throw new EmployeeFileReadException("Error reading employee details from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return employee;
    }

    public List<Employee> readAllEmployees() throws EmployeeFileReadException {
        List<Employee> employees = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 4) {
                    continue; // Skip any malformed lines
                }
                int empId = Integer.parseInt(fields[0]);
                String name = fields[1];
                int age = Integer.parseInt(fields[2]);
                String gender = fields[3];
                Employee employee = new Employee();
                employee.setEmpId(empId);
                employee.setName(name);
                employee.setAge(age);
                employee.setGender(gender);

                employees.add(employee);
            }
        } catch (IOException e) {
            throw new EmployeeFileReadException("Error reading employee details from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return employees;
    }

    public Employee writeEmployee(Employee employee) throws EmployeeFileWriteException, DuplicateEmployeeRecordFoundException, EmployeeFileReadException {
        if (isEmployeeExists(employee.getEmpId())) {
            throw new DuplicateEmployeeRecordFoundException("Employee with Id : " + employee.getEmpId() + " already exists.", 409);
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.file, true));
            String line = String.format("%d,%s,%d,%s",
                    employee.getEmpId(),
                    employee.getName(),
                    employee.getAge(),
                    employee.getGender());

            writer.write(line);
            writer.newLine();
            writer.flush();
            return employee;
        } catch (IOException e) {
            throw new EmployeeFileWriteException("Error writing employee details to file", 500);
        } finally {
            closeBufferedWriter(writer);
        }
    }


    public boolean isEmployeeExists(int empId) throws EmployeeFileReadException {
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

    public Employee deleteEmployee(int empId) throws EmployeeFileDeletionException, EmployeeFileUpdateException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File tempFile = new File(file.getAbsolutePath().replace(".txt", "_temp.txt"));
        Employee deletedEmployee = null;

        try {
            reader = new BufferedReader(new FileReader(this.file));
            writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int currentEmpId = Integer.parseInt(fields[0]);
                if (currentEmpId == empId) {
                    deletedEmployee = new Employee();
                    deletedEmployee.setEmpId(currentEmpId);
                    deletedEmployee.setName(fields[1]);
                    deletedEmployee.setAge(Integer.parseInt(fields[2]));
                    deletedEmployee.setGender(fields[3]);
                    continue;
                }
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            throw new EmployeeFileDeletionException("Error processing employee file for deletion", 500);
        } finally {
            closeBufferedReader(reader);
            closeBufferedWriter(writer);
        }
        if (!renameTo(tempFile, file)) {
            throw new EmployeeFileDeletionException("Error replacing the original file with the updated file after deletion", 500);
        }
        if (deletedEmployee == null) {
            throw new EmployeeFileDeletionException("Employee with ID " + empId + " not found", 404);
        }
        return deletedEmployee;
    }

    public Employee updateEmployee(Employee employee) throws EmployeeFileUpdateException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File tempFile = new File(file.getAbsolutePath().replace(".txt", "_temp.txt"));

        try {
            reader = new BufferedReader(new FileReader(this.file));
            writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int empId = Integer.parseInt(fields[0]);
                if (empId == employee.getEmpId()) {
                    line = String.format("%d,%s,%d,%s",
                            employee.getEmpId(),
                            employee.getName(),
                            employee.getAge(),
                            employee.getGender());
                }
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            throw new EmployeeFileUpdateException("Error updating employee file", 500);
        } finally {
            closeBufferedReader(reader);
            closeBufferedWriter(writer);
        }

        if (!renameTo(tempFile, file)) {
            throw new EmployeeFileUpdateException("Error replacing the original file with the updated file", 500);
        }

        return employee;
    }

    private boolean renameTo(File source, File destination) throws EmployeeFileUpdateException {
        try {
            if (destination.exists() && !destination.delete()) {
                throw new IOException("Failed to delete existing file: " + destination.getAbsolutePath());
            }
            return source.renameTo(destination);
        } catch (IOException e) {
            throw new EmployeeFileUpdateException("Error deleting original file during update", 500);
        }
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


