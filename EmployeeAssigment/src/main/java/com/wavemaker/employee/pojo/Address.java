package com.wavemaker.employee.pojo;

import java.util.Objects;

public class Address {
    private int addressId;
    private String state;
    private String country;
    private int pincode;
    private int empId; //assuming it's a foreign key;
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Address address = (Address) object;
        return addressId == address.addressId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(addressId);
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", pincode=" + pincode +
                '}';
    }
}
