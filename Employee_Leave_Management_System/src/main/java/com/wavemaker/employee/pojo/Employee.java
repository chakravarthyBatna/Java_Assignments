package com.wavemaker.employee.pojo;

import java.util.Date;
import java.util.Objects;

public class Employee {
    private int empId;
    private int managerId;
    private String empName;
    private Date empDateOfBirth;
    private long phoneNumber;
    private String email;
    private String gender;
    private String role;

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getEmpDateOfBirth() {
        return empDateOfBirth;
    }

    public void setEmpDateOfBirth(Date empDateOfBirth) {
        this.empDateOfBirth = empDateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Employee employee = (Employee) object;
        return empId == employee.empId && managerId == employee.managerId && phoneNumber == employee.phoneNumber && Objects.equals(empName, employee.empName) && Objects.equals(empDateOfBirth, employee.empDateOfBirth) && Objects.equals(email, employee.email) && Objects.equals(gender, employee.gender) && Objects.equals(role, employee.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId, managerId, empName, empDateOfBirth, phoneNumber, email, gender, role);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", managerId=" + managerId +
                ", empName='" + empName + '\'' +
                ", empDateOfBirth=" + empDateOfBirth +
                ", phoneNumber=" + phoneNumber +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
