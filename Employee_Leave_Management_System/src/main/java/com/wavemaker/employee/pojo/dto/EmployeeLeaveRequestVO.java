package com.wavemaker.employee.pojo.dto;

import com.wavemaker.employee.constants.LeaveRequestStatus;

import java.util.Date;

public class EmployeeLeaveRequestVO {
    private int leaveRequestId;
    private String leaveType;
    private String leaveReason;
    private Date fromDate;
    private Date toDate;
    private Date dateOfApplication;
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
}
