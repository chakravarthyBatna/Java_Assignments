package com.wavemaker.employee.pojo;

public class LeaveType {
    private int leaveTypeId;
    private String leaveType;
    private String description;
    private int maxNoOfLeaves;
    private String applicableForGender;

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxNoOfLeaves() {
        return maxNoOfLeaves;
    }

    public void setMaxNoOfLeaves(int maxNoOfLeaves) {
        this.maxNoOfLeaves = maxNoOfLeaves;
    }

    public String getApplicableForGender() {
        return applicableForGender;
    }

    public void setApplicableForGender(String applicableForGender) {
        this.applicableForGender = applicableForGender;
    }
}
