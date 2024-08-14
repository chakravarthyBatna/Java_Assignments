package com.wavemaker.printdatatypes;

import com.wavemaker.printdatatypes.pojo.Employee;

public class PrintAllDataTypes {
    public static void main(String[] args) {
        Employee employee = new Employee();
        employee.setEmpId(101);
        employee.setName("Chakravarthy Batna");
        employee.setAge((byte) 23);
        employee.setIdCardNumber((short) 1234);
        employee.setPhoneNumber(9876543210L);
        employee.setGender('M');
        employee.setSalary(55000.50f);
        employee.setEmployed(true);
        employee.setAddressCountryStateCity(new String[]{"India", "AP", "SKLM"});
        System.out.println(employee); //printing all the details
    }
}
