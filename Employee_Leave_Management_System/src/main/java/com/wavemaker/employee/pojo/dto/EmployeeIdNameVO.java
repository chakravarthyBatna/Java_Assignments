package com.wavemaker.employee.pojo.dto;

import java.util.Objects;

public class EmployeeIdNameVO {
    private int empId;
    private String empName;

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EmployeeIdNameVO that = (EmployeeIdNameVO) object;
        return empId == that.empId && Objects.equals(empName, that.empName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId, empName);
    }

    @Override
    public String toString() {
        return "EmployeeIdNameVO{" +
                "empId=" + empId +
                ", empName='" + empName + '\'' +
                '}';
    }
}
