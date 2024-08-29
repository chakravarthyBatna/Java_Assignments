package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.factory.EmployeeLeaveRepositoryInstanceHandler;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.dto.EmployeeLeaveRequestVO;
import com.wavemaker.employee.pojo.dto.LeaveRequestVO;
import com.wavemaker.employee.repository.MyLeaveRepository;
import com.wavemaker.employee.service.MyLeaveService;

import java.sql.SQLException;
import java.util.List;

public class MyLeaveServiceImpl implements MyLeaveService {
    private final MyLeaveRepository myLeaveRepository;

    public MyLeaveServiceImpl() throws SQLException {
        myLeaveRepository = EmployeeLeaveRepositoryInstanceHandler.getEmployeeLeaveRepositoryInstance();
    }

    @Override
    public LeaveRequest applyForLeave(LeaveRequest leaveRequest) throws ServerUnavilableException {
        return myLeaveRepository.applyForLeave(leaveRequest);
    }

    @Override
    public boolean cancelMyLeaveRequest(int leaveRequestId, int approvingEmpId) throws ServerUnavilableException {
        return myLeaveRepository.cancelMyLeaveRequest(leaveRequestId, approvingEmpId);
    }

    @Override
    public List<EmployeeLeaveRequestVO> getMyLeaveRequests(int empId, LeaveRequestStatus status) throws ServerUnavilableException {
        return myLeaveRepository.getMyLeaveRequests(empId, status);
    }

    @Override
    public boolean updateMyLeaveRequest(LeaveRequest leaveRequest) throws ServerUnavilableException {
        return myLeaveRepository.updateMyLeaveRequest(leaveRequest);
    }


}
