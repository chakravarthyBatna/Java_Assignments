package com.wavemaker.employee.util;

import com.wavemaker.employee.exception.employee.EmployeeFileReadException;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.EmployeeRepository;

public class GenerateUniqueId {
    private static volatile int uniqueEmpId = -1;
    private static volatile int uniqueAddressId = -1;

    // private static AtomicInteger uniqueEmpId = new AtomicInteger(-1); //will implement later
    public static synchronized int getUniqueIdForEmployee(EmployeeRepository employeeRepository) throws EmployeeFileReadException {
        uniqueEmpId = employeeRepository.getCount();
        uniqueEmpId = uniqueEmpId + 1;
        return uniqueEmpId;
    }

    public static synchronized int getUniqueForAddress(AddressRepository addressRepository) {
        uniqueAddressId = addressRepository.getCount();
        uniqueAddressId = uniqueAddressId + 1;
        return uniqueAddressId;
    }
}
