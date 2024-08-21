package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.constant.StorageType;
import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.factory.AddressRepositoryFactory;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.service.AddressService;

import java.sql.SQLException;

public class AddressServiceImpl implements AddressService {
    private static AddressRepository addressRepository;
    public AddressServiceImpl(StorageType storageType) throws FileCreationException, SQLException {
        addressRepository = AddressRepositoryFactory.getAddressRepositoryInstance(storageType);
    }

    @Override
    public Address getAddressByEmpId(int empId) throws ServerUnavilableException {
        return addressRepository.getAddressByEmpId(empId);
    }

    @Override
    public Address addAddress(Address address) throws ServerUnavilableException {
        return addressRepository.addAddress(address);
    }

    @Override
    public Address deleteAddressByEmpId(int empId) throws ServerUnavilableException {
        return addressRepository.deleteAddressByEmpId(empId);
    }

    @Override
    public Address updateAddress(Address address) throws ServerUnavilableException {
        return addressRepository.updateAddress(address);
    }
}
