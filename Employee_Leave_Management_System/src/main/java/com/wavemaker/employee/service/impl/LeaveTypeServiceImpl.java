package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.factory.LeaveTypeRepositoryInstanceHandler;
import com.wavemaker.employee.pojo.LeaveType;
import com.wavemaker.employee.repository.LeaveTypeRepository;
import com.wavemaker.employee.service.LeaveTypeService;

import java.sql.SQLException;
import java.util.List;

public class LeaveTypeServiceImpl implements LeaveTypeService {
    private static LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeServiceImpl() throws SQLException {
        leaveTypeRepository = LeaveTypeRepositoryInstanceHandler.getLeaveTypeRepositoryInstance();
    }

    @Override
    public List<LeaveType> getAllLeaveTypes(String gender) throws ServerUnavilableException {
        return leaveTypeRepository.getAllLeaveTypes(gender);
    }

    @Override
    public boolean isLeaveTypeWithInRange(int leaveTypeId, int totalDays) throws ServerUnavilableException, LeaveDaysExceededException {
        return leaveTypeRepository.isLeaveTypeWithInRange(leaveTypeId, totalDays);
    }
}
