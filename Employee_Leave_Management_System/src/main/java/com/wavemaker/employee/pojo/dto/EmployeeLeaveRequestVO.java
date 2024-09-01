package com.wavemaker.employee.pojo.dto;

import com.wavemaker.employee.constants.LeaveRequestStatus;

import java.util.Date;
import java.util.Objects;

public class EmployeeLeaveRequestVO {
    private int leaveRequestId;
    private String leaveType;
    private String leaveReason;
    private Date fromDate;
    private Date toDate;
    private Date dateOfApplication;
    private int totalDays;
    private LeaveRequestStatus leaveRequestStatus;
    private Date dateOfApproved;

    public int getLeaveRequestId() {
        return leaveRequestId;
    }

    public void setLeaveRequestId(int leaveRequestId) {
        this.leaveRequestId = leaveRequestId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
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

    public Date getDateOfApplication() {
        return dateOfApplication;
    }

    public void setDateOfApplication(Date dateOfApplication) {
        this.dateOfApplication = dateOfApplication;
    }

    public LeaveRequestStatus getLeaveRequestStatus() {
        return leaveRequestStatus;
    }

    public void setLeaveRequestStatus(LeaveRequestStatus leaveRequestStatus) {
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
        EmployeeLeaveRequestVO that = (EmployeeLeaveRequestVO) object;
        return leaveRequestId == that.leaveRequestId && Objects.equals(leaveType, that.leaveType) && Objects.equals(leaveReason, that.leaveReason) && Objects.equals(fromDate, that.fromDate) && Objects.equals(toDate, that.toDate) && Objects.equals(dateOfApplication, that.dateOfApplication) && leaveRequestStatus == that.leaveRequestStatus && Objects.equals(dateOfApproved, that.dateOfApproved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaveRequestId, leaveType, leaveReason, fromDate, toDate, dateOfApplication, leaveRequestStatus, dateOfApproved);
    }

    @Override
    public String toString() {
        return "EmployeeLeaveRequestVO{" +
                "leaveRequestId=" + leaveRequestId +
                ", leaveType='" + leaveType + '\'' +
                ", leaveReason='" + leaveReason + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", dateOfApplication=" + dateOfApplication +
                ", totalDays=" + totalDays +
                ", leaveRequestStatus=" + leaveRequestStatus +
                ", dateOfApproved=" + dateOfApproved +
                '}';
    }
}
