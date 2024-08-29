package com.wavemaker.employee.service;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.LeaveType;

import java.util.List;

public interface LeaveTypeService {
    public List<LeaveType> getAllLeaveTypes() throws ServerUnavilableException;
}
