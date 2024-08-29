package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.LeaveType;
import com.wavemaker.employee.repository.LeaveTypeRepository;
import com.wavemaker.employee.repository.impl.LeaveTypeRepositoryImpl;
import com.wavemaker.employee.service.LeaveTypeService;

import java.sql.SQLException;
import java.util.List;

public class LeaveTypeServiceImpl implements LeaveTypeService {
    private static LeaveTypeRepository leaveTypeRepository;
    public LeaveTypeServiceImpl() throws SQLException {
        leaveTypeRepository = new LeaveTypeRepositoryImpl();
    }
    @Override
    public List<LeaveType> getAllLeaveTypes() throws ServerUnavilableException {
        return leaveTypeRepository.getAllLeaveTypes();
    }
}
