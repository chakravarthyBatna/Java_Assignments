package com.wavemaker.employee.service;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.LeaveType;
import com.wavemaker.employee.pojo.dto.EmployeeLeaveRequestVO;
import com.wavemaker.employee.pojo.dto.LeaveRequestVO;

import java.util.List;

public interface MyLeaveService {

    public LeaveRequest applyForLeave(LeaveRequest leaveRequest) throws ServerUnavilableException;

    public boolean cancelMyLeaveRequest(int leaveRequestId, int approvingEmpId) throws ServerUnavilableException;

    public List<EmployeeLeaveRequestVO> getMyLeaveRequests(int empId, LeaveRequestStatus status) throws ServerUnavilableException;

    public boolean updateMyLeaveRequest(LeaveRequest leaveRequest) throws ServerUnavilableException;
}
