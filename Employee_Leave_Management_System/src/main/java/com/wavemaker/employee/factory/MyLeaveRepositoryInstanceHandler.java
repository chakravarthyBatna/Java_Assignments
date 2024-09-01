package com.wavemaker.employee.factory;

import com.wavemaker.employee.repository.MyLeaveRepository;
import com.wavemaker.employee.repository.impl.MyLeaveRepositoryImpl;

import java.sql.SQLException;

public class MyLeaveRepositoryInstanceHandler {
    private static volatile MyLeaveRepository myLeaveRepository;

    public static MyLeaveRepository getEmployeeLeaveRepositoryInstance() throws SQLException {
        if (myLeaveRepository == null) {
            synchronized (MyLeaveRepositoryInstanceHandler.class) {
                if (myLeaveRepository == null) {
                    myLeaveRepository = new MyLeaveRepositoryImpl();
                }
            }
        }
        return myLeaveRepository;
    }
}
