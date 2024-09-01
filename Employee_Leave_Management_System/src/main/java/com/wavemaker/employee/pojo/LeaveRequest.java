package com.wavemaker.employee.pojo;

import com.wavemaker.employee.constants.LeaveRequestStatus;

import java.util.Date;
import java.util.Objects;

public class LeaveRequest {
    private int leaveRequestId;
    private int empId;
    private int leaveTypeId;
    private String leaveReason;
    private Date fromDate;
    private Date toDate;
    private Date dateOfApplication;
    private int totalNoOfDays;
    private LeaveRequestStatus leaveRequestStatus;
    private Date dateOfApproved;

    public int getLeaveRequestId() {
        return leaveRequestId;
    }

    public void setLeaveRequestId(int leaveRequestId) {
        this.leaveRequestId = leaveRequestId;
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

    public Date getDateOfApplication() {
        return dateOfApplication;
    }

    public void setDateOfApplication(Date dateOfApplication) {
        this.dateOfApplication = dateOfApplication;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public int getTotalNoOfDays() {
        return totalNoOfDays;
    }

    public void setTotalNoOfDays(int totalNoOfDays) {
        this.totalNoOfDays = totalNoOfDays;
    }

    public LeaveRequestStatus getLeaveRequestStatus() {
        return leaveRequestStatus;
    }

    public void setLeaveRequestStatus(LeaveRequestStatus leaveRequestStatus) {
        this.leaveRequestStatus = leaveRequestStatus;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public LeaveRequestStatus getLeaveStatus() {
        return leaveRequestStatus;
    }

    public void setLeaveStatus(LeaveRequestStatus leaveRequestStatus) {
        this.leaveRequestStatus = leaveRequestStatus;
    }

    public Date getDateOfApproved() {
        return dateOfApproved;
    }

    public void setDateOfApproved(Date dateOfApproved) {
        this.dateOfApproved = dateOfApproved;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        LeaveRequest that = (LeaveRequest) object;
        return leaveRequestId == that.leaveRequestId && empId == that.empId && leaveTypeId == that.leaveTypeId && totalNoOfDays == that.totalNoOfDays && Objects.equals(leaveReason, that.leaveReason) && Objects.equals(fromDate, that.fromDate) && Objects.equals(toDate, that.toDate) && Objects.equals(dateOfApplication, that.dateOfApplication) && leaveRequestStatus == that.leaveRequestStatus && Objects.equals(dateOfApproved, that.dateOfApproved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaveRequestId, empId, leaveTypeId, leaveReason, fromDate, toDate, dateOfApplication, totalNoOfDays, leaveRequestStatus, dateOfApproved);
    }

    @Override
    public String toString() {
        return "LeaveRequest{" +
                "leaveRequestId=" + leaveRequestId +
                ", empId=" + empId +
                ", leaveTypeId=" + leaveTypeId +
                ", leaveReason='" + leaveReason + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", dateOfApplication=" + dateOfApplication +
                ", totalNoOfDays=" + totalNoOfDays +
                ", leaveRequestStatus=" + leaveRequestStatus +
                ", dateOfApproved=" + dateOfApproved +
                '}';
    }
}
