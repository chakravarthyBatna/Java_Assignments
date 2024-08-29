package com.wavemaker.employee.pojo.dto;

import java.util.Date;

public class EmployeeVO {
    private int empId;
    private int managerId;
    private String managerName;
    private String managerPhoneNumber;
    private String managerEmailId;
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

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerPhoneNumber() {
        return managerPhoneNumber;
    }

    public void setManagerPhoneNumber(String managerPhoneNumber) {
        this.managerPhoneNumber = managerPhoneNumber;
    }

    public String getManagerEmailId() {
        return managerEmailId;
    }

    public void setManagerEmailId(String managerEmailId) {
        this.managerEmailId = managerEmailId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Date getEmpDateOfBirth() {
        return empDateOfBirth;
    }

    public void setEmpDateOfBirth(Date empDateOfBirth) {
        this.empDateOfBirth = empDateOfBirth;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
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
}
