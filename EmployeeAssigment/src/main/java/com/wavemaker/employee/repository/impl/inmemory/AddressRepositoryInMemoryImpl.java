package com.wavemaker.employee.repository.impl.inmemory;

import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.repository.AddressRepository;

import java.util.concurrent.ConcurrentHashMap;

public class AddressRepositoryInMemoryImpl implements AddressRepository {
    //key = empId(foreign key) and value = address object
    ConcurrentHashMap<Integer, Address> addressMap = new ConcurrentHashMap<>();


    @Override
    public Address getAddressByEmpId(int empId) {
        return addressMap.get(empId);
    }

    @Override
    public boolean addAddress(Address address) {
        Address addedAddress = addressMap.put(address.getEmpId(),address);
        return addedAddress != null;
    }

    @Override
    public Address deleteAddressByEmpId(int empId) {
        return addressMap.remove(empId);
    }

    @Override
    public Address updateAddress(Address address) {
        Address updatedAddress = null;
        if (addressMap.containsKey(address.getEmpId())) {
            updatedAddress = addressMap.put(address.getEmpId(), address);
        }
        return updatedAddress;
    }
}
