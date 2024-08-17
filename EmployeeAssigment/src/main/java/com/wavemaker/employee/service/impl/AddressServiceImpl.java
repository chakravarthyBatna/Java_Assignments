package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.constant.Storage_Type;
import com.wavemaker.employee.factory.AddressRepositoryFactory;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.service.AddressService;

public class AddressServiceImpl implements AddressService {
    private static AddressRepository addressRepository;
    public AddressServiceImpl(Storage_Type storageType) {
        addressRepository = AddressRepositoryFactory.getAddressRepositoryInstance(storageType);
    }

    @Override
    public Address getAddressByEmpId(int empId) {
        return addressRepository.getAddressByEmpId(empId);
    }

    @Override
    public boolean addAddress(Address address) {
        return addressRepository.addAddress(address);
    }

    @Override
    public Address deleteAddressByEmpId(int empId) {
        return addressRepository.deleteAddressByEmpId(empId);
    }

    @Override
    public Address updateAddress(Address address) {
        return addressRepository.updateAddress(address);
    }
}
