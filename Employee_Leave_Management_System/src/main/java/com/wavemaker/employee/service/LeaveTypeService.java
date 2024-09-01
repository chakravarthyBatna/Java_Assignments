package com.wavemaker.employee.service;

import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.LeaveType;

import java.util.List;

public interface LeaveTypeService {
    public List<LeaveType> getAllLeaveTypes(String gender) throws ServerUnavilableException;

    public boolean isLeaveTypeWithInRange(int leaveTypeId, int totalDays) throws ServerUnavilableException, LeaveDaysExceededException;
}
