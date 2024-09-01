package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.factory.EmployeeLeaveSummaryRepositoryInstanceHandler;
import com.wavemaker.employee.pojo.EmployeeLeaveSummary;
import com.wavemaker.employee.pojo.dto.EmployeeIdNameVO;
import com.wavemaker.employee.repository.EmployeeLeaveSummaryRepository;
import com.wavemaker.employee.service.EmployeeLeaveSummaryService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class EmployeeLeaveSummaryServiceImpl implements EmployeeLeaveSummaryService {
    private final EmployeeLeaveSummaryRepository employeeLeaveSummaryRepository;

    public EmployeeLeaveSummaryServiceImpl() throws SQLException {
        employeeLeaveSummaryRepository = EmployeeLeaveSummaryRepositoryInstanceHandler.getEmployeeLeaveSummaryRepositoryInstance();
    }

    @Override
    public boolean updateEmployeeLeaveSummary(int empId) throws ServerUnavilableException {
        return employeeLeaveSummaryRepository.updateEmployeeLeaveSummary(empId);
    }

    @Override
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummariesById(int empId) throws ServerUnavilableException {
        return employeeLeaveSummaryRepository.getEmployeeLeaveSummariesById(empId);
    }

    @Override
    public Map<EmployeeIdNameVO, List<EmployeeLeaveSummary>> getAllEmployeesLeaveSummary(int empId) throws ServerUnavilableException {
        return employeeLeaveSummaryRepository.getAllEmployeesLeaveSummary(empId);
    }
}
