package com.wavemaker.employee.service;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.Address;

public interface AddressService {
    public Address getAddressByEmpId(int empId) throws ServerUnavilableException;
    public Address addAddress(Address address) throws ServerUnavilableException;
    public Address deleteAddressByEmpId(int empId) throws ServerUnavilableException;
    public Address updateAddress(Address address) throws ServerUnavilableException;
}
