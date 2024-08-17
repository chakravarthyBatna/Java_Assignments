package com.wavemaker.employee.repository;

import com.wavemaker.employee.model.Address;

public interface AddressRepository {
//    public int (int empId);
    public boolean addAddress(Address address);
    public Address deleteAddress(int addressId);
    public Address updateAddress(Address address);
}
