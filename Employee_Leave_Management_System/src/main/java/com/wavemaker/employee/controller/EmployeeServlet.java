package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.dto.EmployeeVO;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;
import com.wavemaker.employee.util.ClientResponseHandler;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

@WebServlet("/employee")
public class EmployeeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServlet.class);
    private static EmployeeService employeeService;
    private static Gson gson = null;

    @Override
    public void init(ServletConfig config) {
        try {
            gson = new Gson();
            employeeService = new EmployeeServiceImpl();
        } catch (SQLException e) {
            logger.error("Exception", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        String jsonResponse = null;
        List<Employee> employeeList = null;
        String empId = httpServletRequest.getParameter("empId");
        String action = httpServletRequest.getParameter("action");
        logger.info("Received GET request with empId: {}", empId);
        try {
            if (empId != null) {
                logger.info("Fetching employee details for empId: {}", empId);
                EmployeeVO employee = employeeService.getEmployeeById(Integer.parseInt(empId));
                jsonResponse = gson.toJson(employee);
                ClientResponseHandler.sendResponseToClient(response, jsonResponse, logger);
            }
            if (action.equals("getManagers")) {
                employeeList = employeeService.getAllManagers();
                jsonResponse = gson.toJson(employeeList);
            } else {
                employeeList = employeeService.getEmployees();
                jsonResponse = gson.toJson(employeeList);
            }
        } catch (ServerUnavilableException e) {
            logger.error("Error fetching Employee details", e);
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
            jsonResponse = gson.toJson(errorResponse);
        } finally {
            ClientResponseHandler.sendResponseToClient(response, jsonResponse, logger);
        }
    }
}
