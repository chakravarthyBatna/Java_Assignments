package com.wavemaker.employee.pojo;

import java.util.Date;
import java.util.Objects;

public class EmployeeLeaveSummary {
    public int summaryId;
    public int empId;
    public int leaveTypeId;
    public String leaveType;
    public int pendingLeaves;
    public int totalLeavesTaken;
    public Date lastUpdated;

    public int getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(int summaryId) {
        this.summaryId = summaryId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

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

    public int getPendingLeaves() {
        return pendingLeaves;
    }

    public void setPendingLeaves(int pendingLeaves) {
        this.pendingLeaves = pendingLeaves;
    }

    public int getTotalLeavesTaken() {
        return totalLeavesTaken;
    }

    public void setTotalLeavesTaken(int totalLeavesTaken) {
        this.totalLeavesTaken = totalLeavesTaken;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EmployeeLeaveSummary that = (EmployeeLeaveSummary) object;
        return summaryId == that.summaryId && empId == that.empId && leaveTypeId == that.leaveTypeId && pendingLeaves == that.pendingLeaves && totalLeavesTaken == that.totalLeavesTaken && Objects.equals(leaveType, that.leaveType) && Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summaryId, empId, leaveTypeId, leaveType, pendingLeaves, totalLeavesTaken, lastUpdated);
    }

    @Override
    public String toString() {
        return "EmployeeLeaveSummary{" +
                "summaryId=" + summaryId +
                ", empId=" + empId +
                ", leaveTypeId=" + leaveTypeId +
                ", leaveType='" + leaveType + '\'' +
                ", pendingLeaves=" + pendingLeaves +
                ", totalLeavesTaken=" + totalLeavesTaken +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
