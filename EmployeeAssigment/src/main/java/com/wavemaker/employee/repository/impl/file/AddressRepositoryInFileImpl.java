package com.wavemaker.employee.repository.impl.file;

import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.util.AddressCSVFileReadAndWrite;
import com.wavemaker.employee.util.FileCreateUtil;

public class AddressRepositoryInFileImpl implements AddressRepository {
    private static final String FILE_PATH = "C:\\Users\\chakraarthyb_700063\\IdeaProjects\\Java_Assignments\\EmployeeAddress.txt";
    private static AddressCSVFileReadAndWrite addressCSVFileReadAndWrite;

    public AddressRepositoryInFileImpl() throws FileCreationException {
        addressCSVFileReadAndWrite = new AddressCSVFileReadAndWrite(FileCreateUtil.createFileIfNotExists(FILE_PATH));
    }

    @Override
    public Address getAddressByEmpId(int empId) {
        return addressCSVFileReadAndWrite.getAddressByEmpId(empId);
    }

    @Override
    public boolean addAddress(Address address) {
        return addressCSVFileReadAndWrite.addAddress(address);
    }

    @Override
    public Address deleteAddressByEmpId(int empId) {
        return addressCSVFileReadAndWrite.deleteAddressByEmpId(empId);
    }

    @Override
    public Address updateAddress(Address address) {
        return addressCSVFileReadAndWrite.updateAddress(address);
    }

    @Override
    public int getCount() {
        return addressCSVFileReadAndWrite.getNoOfLinesInAFile();
    }
}
