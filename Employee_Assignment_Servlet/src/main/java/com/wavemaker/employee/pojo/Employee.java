package com.wavemaker.employee.pojo;

import java.io.Serializable;
import java.util.Objects;

public class Employee implements Cloneable, Serializable, Comparable<Employee>{
    private int empId;
    private String name;
    private String gender;
    private int age;
    private String email;
    private Address address;

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Employee employee = (Employee) object;
        return empId == employee.empId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(empId);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", address=" + address +
                '}';
    }

    @Override
    public Employee clone() {
        Employee cloned = null;
        try {
            cloned = (Employee) super.clone();
            cloned.setAddress(address != null ? address.clone() : null);
            return cloned;
        } catch (CloneNotSupportedException e) {
            System.out.println(e.getMessage());
        }
        return cloned;
    }
    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.empId, other.empId);
    }
}
