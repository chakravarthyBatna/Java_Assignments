package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.dto.EmployeeVO;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
@WebServlet("/employee")
public class EmployeeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServlet.class);
    private static EmployeeService employeeService;
    private static Gson gson = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            gson = new Gson();
            employeeService = new EmployeeServiceImpl();
        } catch (SQLException e) {
            logger.error("Exception", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonEmployeeResponse = null;
        List<Employee> employeeList = null;
        String empId = httpServletRequest.getParameter("empId");
        String action = httpServletRequest.getParameter("action");
        logger.info("Received GET request with empId: {}", empId);
        try {
            if (empId != null) {
                logger.info("Fetching employee details for empId: {}", empId);
                EmployeeVO employee = employeeService.getEmployeeById(Integer.parseInt(empId));
                jsonEmployeeResponse = gson.toJson(employee);
                sendResponse(httpServletResponse, jsonEmployeeResponse);
                logger.info("Successfully fetched employee details for emp: {}", employee);
            }
            if (action.equals("getManagers")) {
                employeeList = employeeService.getAllManagers();
                jsonEmployeeResponse = gson.toJson(employeeList);
            } else {
                employeeList = employeeService.getEmployees();
                jsonEmployeeResponse = gson.toJson(employeeList);
            }
        } catch (ServerUnavilableException e) {
            logger.error("Error fetching Employee details", e);
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
            jsonEmployeeResponse = gson.toJson(errorResponse);
        }
        sendResponse(httpServletResponse, jsonEmployeeResponse);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }


    private void sendResponse(HttpServletResponse httpServletResponse, String jsonResponse) {
        PrintWriter printWriter = null;
        try {
            logger.info("Preparing response to send back to client");
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            printWriter = httpServletResponse.getWriter();
            printWriter.print(jsonResponse);
            printWriter.flush();
            logger.info("Response successfully sent back to client");
        } catch (IOException e) {
            logger.error("Error writing response back to client", e);
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", 500);
            jsonResponse = gson.toJson(errorResponse);
            httpServletResponse.setStatus(500);
            printWriter.print(jsonResponse);
            printWriter.flush();
        } finally {
            closePrintWriter(printWriter);
        }
    }

    private void closePrintWriter(PrintWriter printWriter) {
        if (printWriter != null) {
            printWriter.close();
        }
    }

    private void closeBufferReader(BufferedReader bufferedReader) throws IOException {
        if (bufferedReader != null) {
            bufferedReader.close();
        }
    }
}
