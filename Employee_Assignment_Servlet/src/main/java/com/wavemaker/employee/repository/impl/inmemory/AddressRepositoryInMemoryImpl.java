package com.wavemaker.employee.repository.impl.inmemory;

import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.repository.AddressRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AddressRepositoryInMemoryImpl implements AddressRepository {
    //key = empId(foreign key) and value = address object
    private static final ConcurrentMap<Integer, Address> addressMap = new ConcurrentHashMap<>();
    private static int maxAddressId = 0;
    @Override
    public Address getAddressByEmpId(int empId) {
        return addressMap.get(empId);
    }

    @Override
    public Address addAddress(Address address) {
        if (maxAddressId == 0) {
            maxAddressId = generateAddressId();
        } else {
            maxAddressId += 1;
        }
        address.setAddressId(maxAddressId);
        address = addressMap.put(address.getEmpId(),address);
        return address;
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
    private int generateAddressId() {
        for (Address address : addressMap.values()) {
            maxAddressId = Math.max(maxAddressId, address.getAddressId());
        }
        return maxAddressId + 1;
    }
}
