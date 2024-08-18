package com.wavemaker.employee.repository;

import com.wavemaker.employee.pojo.Address;

public interface AddressRepository {
    public Address getAddressByEmpId(int empId);
    public boolean addAddress(Address address);
    public Address deleteAddressByEmpId(int empId);
    public Address updateAddress(Address address);
    public int getCount();
}
