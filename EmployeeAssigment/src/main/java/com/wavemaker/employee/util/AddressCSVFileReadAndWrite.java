package com.wavemaker.employee.util;

import com.wavemaker.employee.exception.address.*;
import com.wavemaker.employee.pojo.Address;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AddressCSVFileReadAndWrite {
    private final File file;

    public AddressCSVFileReadAndWrite(File file) {
        this.file = file;
    }

    public Address getAddressByEmpId(int empId) throws AddressFileReadException{
        BufferedReader reader = null;
        Address address = null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    continue; // Skip any malformed lines
                }
                int currentEmpId = Integer.parseInt(fields[1]);
                if (currentEmpId == empId) {
                    int addressId = Integer.parseInt(fields[0]);
                    String country = fields[2];
                    String state = fields[3];
                    int pincode = Integer.parseInt(fields[4]);

                    address = new Address();
                    address.setAddressId(addressId);
                    address.setEmpId(currentEmpId);
                    address.setCountry(country);
                    address.setState(state);
                    address.setPincode(pincode);

                    return address;
                }
            }
        } catch (IOException e) {
            throw new AddressFileReadException("Error reading address details from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return address;
    }

    public List<Address> readAllAddresses() throws AddressFileReadException {
        List<Address> addresses = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    continue; // Skip any malformed lines
                }
                int addressId = Integer.parseInt(fields[0]);
                int empId = Integer.parseInt(fields[1]);
                String country = fields[2];
                String state = fields[3];
                int pincode = Integer.parseInt(fields[4]);

                Address address = new Address();
                address.setAddressId(addressId);
                address.setEmpId(empId);
                address.setCountry(country);
                address.setState(state);
                address.setPincode(pincode);

                addresses.add(address);
            }
        } catch (IOException e) {
            throw new AddressFileReadException("Error reading address details from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return addresses;
    }

    public boolean addAddress(Address address) throws AddressFileWriteException, DuplicateAddressRecordFoundException {
        if (isAddressExistsForEmpId(address.getEmpId())) {
            throw new DuplicateAddressRecordFoundException("Address for Employee with ID: " + address.getEmpId() + " already exists.", 409);
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.file, true));
            String line = String.format("%d,%d,%s,%s,%d",
                    address.getAddressId(),
                    address.getEmpId(),
                    address.getCountry(),
                    address.getState(),
                    address.getPincode());

            writer.write(line);
            writer.newLine();
            writer.flush();
            return true;
        } catch (IOException e) {
            throw new AddressFileWriteException("Error writing address details to file", 500);
        } finally {
            closeBufferedWriter(writer);
        }
    }

    public boolean isAddressExistsForEmpId(int empId) throws AddressFileReadException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    continue; // Skip any malformed lines
                }
                int existingEmpId = Integer.parseInt(fields[1]);
                if (existingEmpId == empId) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new AddressFileReadException("Failed to read address data from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return false;
    }

    public Address deleteAddressByEmpId(int empId) throws AddressFileDeletionException, AddressNotFoundException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File tempFile = new File(file.getAbsolutePath().replace(".txt", "_temp.txt"));
        Address deletedAddress = null;

        try {
            reader = new BufferedReader(new FileReader(this.file));
            writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    continue; // Skip any malformed lines
                }
                int currentEmpId = Integer.parseInt(fields[1]);
                if (currentEmpId == empId) {
                    deletedAddress = new Address();
                    deletedAddress.setAddressId(Integer.parseInt(fields[0]));
                    deletedAddress.setEmpId(currentEmpId);
                    deletedAddress.setCountry(fields[2]);
                    deletedAddress.setState(fields[3]);
                    deletedAddress.setPincode(Integer.parseInt(fields[4]));
                    continue;
                }
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            throw new AddressFileDeletionException("Error processing address file for deletion", 500);
        } finally {
            closeBufferedReader(reader);
            closeBufferedWriter(writer);
        }
        if (!renameTo(tempFile, file)) {
            throw new AddressFileDeletionException("Error replacing the original file with the updated file after deletion", 500);
        }
        if (deletedAddress == null) {
            throw new AddressFileDeletionException("Address for Employee with ID " + empId + " not found", 404);
        }
        return deletedAddress;
    }

    public Address updateAddress(Address address) throws AddressFileUpdateException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File tempFile = new File(file.getAbsolutePath().replace(".txt", "_temp.txt"));

        try {
            reader = new BufferedReader(new FileReader(this.file));
            writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int currentEmpId = Integer.parseInt(fields[1]);
                if (currentEmpId == address.getEmpId()) {
                    line = String.format("%d,%d,%s,%s,%d",
                            address.getAddressId(),
                            address.getEmpId(),
                            address.getCountry(),
                            address.getState(),
                            address.getPincode());
                }
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            throw new AddressFileUpdateException("Error updating address file", 500);
        } finally {
            closeBufferedReader(reader);
            closeBufferedWriter(writer);
        }

        if (!renameTo(tempFile, file)) {
            throw new AddressFileUpdateException("Error replacing the original file with the updated file", 500);
        }

        return address;
    }

    public int getNoOfLinesInAFile() throws AddressFileReadException {
        BufferedReader reader = null;
        int lineCount = 0;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            while (reader.readLine() != null) {
                lineCount++;
            }
        } catch (IOException e) {
            throw new AddressFileReadException("Error reading the file to count lines", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return lineCount;
    }


    private boolean renameTo(File source, File destination) throws AddressFileUpdateException {
        try {
            if (destination.exists() && !destination.delete()) {
                throw new IOException("Failed to delete existing file: " + destination.getAbsolutePath());
            }
            return source.renameTo(destination);
        } catch (IOException e) {
            throw new AddressFileUpdateException("Error deleting original file during update", 500);
        }
    }

    private void closeBufferedReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Failed to close the reader: " + e.getMessage());
            }
        }
    }

    private void closeBufferedWriter(BufferedWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Failed to close the writer: " + e.getMessage());
            }
        }
    }
}
