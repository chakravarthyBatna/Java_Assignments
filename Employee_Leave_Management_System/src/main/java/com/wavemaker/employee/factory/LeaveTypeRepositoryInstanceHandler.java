package com.wavemaker.employee.factory;

import com.wavemaker.employee.repository.LeaveTypeRepository;
import com.wavemaker.employee.repository.impl.LeaveTypeRepositoryImpl;

import java.sql.SQLException;

public class LeaveTypeRepositoryInstanceHandler {
    private static volatile LeaveTypeRepository leaveTypeRepository;

    public static LeaveTypeRepository getLeaveTypeRepositoryInstance() throws SQLException {
        if (leaveTypeRepository == null) {
            synchronized (LeaveTypeRepositoryInstanceHandler.class) {
                if (leaveTypeRepository == null) {
                    leaveTypeRepository = new LeaveTypeRepositoryImpl();
                }
            }
        }
        return leaveTypeRepository;
    }
}
