package com.wavemaker.printdatatypes.pojo;

import java.util.Arrays;

public class Employee {
    private int empId;
    private String name;
    private byte age;
    private short idCardNumber;
    private long phoneNumber;
    private char gender;
    private float salary;
    private boolean isEmployed;
    private String[] addressCountryStateCity;
    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getAge() {
        return age;
    }

    public void setAge(byte age) {
        this.age = age;
    }

    public short getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(short idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public boolean isEmployed() {
        return isEmployed;
    }

    public void setEmployed(boolean employed) {
        isEmployed = employed;
    }

    public String[] getAddressCountryStateCity() {
        return addressCountryStateCity;
    }

    public void setAddressCountryStateCity(String[] addressCountryStateCity) {
        this.addressCountryStateCity = addressCountryStateCity;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", idCardNumber=" + idCardNumber +
                ", phoneNumber=" + phoneNumber +
                ", gender=" + gender +
                ", salary=" + salary +
                ", isEmployed=" + isEmployed +
                ", addressCountryStateCity=" + Arrays.toString(addressCountryStateCity) +
                '}';
    }
}
