package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.dto.LeaveRequestVO;
import com.wavemaker.employee.repository.MyTeamLeaveRepository;
import com.wavemaker.employee.repository.impl.MyTeamLeaveRepositoryImpl;
import com.wavemaker.employee.service.MyTeamLeaveService;

import java.util.List;

public class MyTeamLeaveServiceImpl implements MyTeamLeaveService {
    private MyTeamLeaveRepository myTeamLeaveRepository;

    public MyTeamLeaveServiceImpl() {
        myTeamLeaveRepository = new MyTeamLeaveRepositoryImpl();
    }
    @Override
    public List<LeaveRequestVO> getMyTeamLeaveRequests(int managerEmpId, String status) throws ServerUnavilableException {
        return myTeamLeaveRepository.getMyTeamLeaveRequests(managerEmpId, status);
    }
    @Override
    public boolean approveOrRejectTeamLeaveRequest(int leaveRequestId, int approvingEmpId, LeaveRequestStatus approveOrRejectOrCancel) throws ServerUnavilableException {
        return myTeamLeaveRepository.approveOrRejectTeamLeaveRequest(leaveRequestId, approvingEmpId, approveOrRejectOrCancel);
    }
}
