package com.wavemaker.employee.factory;

import com.wavemaker.employee.repository.MyLeaveRepository;
import com.wavemaker.employee.repository.impl.MyLeaveRepositoryImpl;

import java.sql.SQLException;

public class EmployeeLeaveRepositoryInstanceHandler {
    private static volatile MyLeaveRepository myLeaveRepository;
    public static MyLeaveRepository getEmployeeLeaveRepositoryInstance() throws SQLException {
        if (myLeaveRepository == null) {
            synchronized (EmployeeLeaveRepositoryInstanceHandler.class) {
                if (myLeaveRepository == null) {
                    myLeaveRepository = new MyLeaveRepositoryImpl();
                }
            }
        }
        return myLeaveRepository;
    }
}
