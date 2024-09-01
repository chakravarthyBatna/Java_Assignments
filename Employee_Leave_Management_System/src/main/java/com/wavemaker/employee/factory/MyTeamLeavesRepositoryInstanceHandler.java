package com.wavemaker.employee.factory;

import com.wavemaker.employee.repository.MyTeamLeaveRepository;
import com.wavemaker.employee.repository.impl.MyTeamLeaveRepositoryImpl;

public class MyTeamLeavesRepositoryInstanceHandler {
    private static volatile MyTeamLeaveRepository myTeamLeaveRepository;

    public static MyTeamLeaveRepository getMyTeamLeaveRepositoryInstance() {
        if (myTeamLeaveRepository == null) {
            synchronized (MyTeamLeavesRepositoryInstanceHandler.class) {
                if (myTeamLeaveRepository == null) {
                    myTeamLeaveRepository = new MyTeamLeaveRepositoryImpl();
                }
            }
        }
        return myTeamLeaveRepository;
    }
}
