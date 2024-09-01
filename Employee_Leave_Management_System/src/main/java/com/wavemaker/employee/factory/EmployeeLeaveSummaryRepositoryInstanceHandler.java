package com.wavemaker.employee.factory;

import com.wavemaker.employee.repository.EmployeeLeaveSummaryRepository;
import com.wavemaker.employee.repository.impl.EmployeeLeaveSummaryRepositoryImpl;

import java.sql.SQLException;

public class EmployeeLeaveSummaryRepositoryInstanceHandler {
    private static volatile EmployeeLeaveSummaryRepository employeeLeaveSummaryRepository;

    public static EmployeeLeaveSummaryRepository getEmployeeLeaveSummaryRepositoryInstance() throws SQLException {
        if (employeeLeaveSummaryRepository == null) {
            synchronized (EmployeeLeaveSummaryRepositoryInstanceHandler.class) {
                if (employeeLeaveSummaryRepository == null) {
                    employeeLeaveSummaryRepository = new EmployeeLeaveSummaryRepositoryImpl();
                }
            }
        }
        return employeeLeaveSummaryRepository;
    }
}
