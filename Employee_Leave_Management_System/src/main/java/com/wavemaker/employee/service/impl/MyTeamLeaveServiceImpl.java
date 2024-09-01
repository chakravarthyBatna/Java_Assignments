package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.factory.MyTeamLeavesRepositoryInstanceHandler;
import com.wavemaker.employee.pojo.dto.LeaveRequestVO;
import com.wavemaker.employee.repository.MyTeamLeaveRepository;
import com.wavemaker.employee.service.EmployeeLeaveSummaryService;
import com.wavemaker.employee.service.MyLeaveService;
import com.wavemaker.employee.service.MyTeamLeaveService;

import java.sql.SQLException;
import java.util.List;

public class MyTeamLeaveServiceImpl implements MyTeamLeaveService {
    private final MyTeamLeaveRepository myTeamLeaveRepository;
    private MyLeaveService myLeaveService = null;
    private EmployeeLeaveSummaryService employeeLeaveSummaryService = null;

    public MyTeamLeaveServiceImpl() throws SQLException {
        myLeaveService = new MyLeaveServiceImpl();
        employeeLeaveSummaryService = new EmployeeLeaveSummaryServiceImpl();
        myTeamLeaveRepository = MyTeamLeavesRepositoryInstanceHandler.getMyTeamLeaveRepositoryInstance();
    }

    @Override
    public List<LeaveRequestVO> getMyTeamLeaveRequests(int managerEmpId, String status) throws ServerUnavilableException {
        return myTeamLeaveRepository.getMyTeamLeaveRequests(managerEmpId, status);
    }

    @Override
    public boolean approveOrRejectTeamLeaveRequest(int leaveRequestId, int approvingEmpId, LeaveRequestStatus approveOrRejectOrCancel) throws ServerUnavilableException {
        int empId = myLeaveService.getEmployeeIdByLeaveRequestId(leaveRequestId);
        if (approveOrRejectOrCancel.equals(LeaveRequestStatus.APPROVED)) {
            employeeLeaveSummaryService.updateEmployeeLeaveSummary(empId);
        }
        return myTeamLeaveRepository.approveOrRejectTeamLeaveRequest(leaveRequestId, approvingEmpId, approveOrRejectOrCancel);
    }
}
