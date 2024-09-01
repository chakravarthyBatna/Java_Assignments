package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.LeaveType;
import com.wavemaker.employee.service.LeaveTypeService;
import com.wavemaker.employee.service.impl.LeaveTypeServiceImpl;
import com.wavemaker.employee.util.ClientResponseHandler;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/leavetypes")
public class LeaveTypeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LeaveTypeServlet.class);
    private static LeaveTypeService leaveTypeService;
    private Gson gson = null;

    @Override
    public void init(ServletConfig config) {
        try {
            leaveTypeService = new LeaveTypeServiceImpl();
            gson = new Gson();
        } catch (SQLException e) {
            logger.error("Exception : ", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        String gender = request.getParameter("gender");
        List<LeaveType> leaveTypeList = null;
        String jsonResponse = null;
        try {
            leaveTypeList = leaveTypeService.getAllLeaveTypes(gender);
            jsonResponse = gson.toJson(leaveTypeList);
        } catch (ServerUnavilableException e) {
            ErrorResponse errorResponse = new ErrorResponse("Error occurred while retrieving leave types", 500);
            jsonResponse = gson.toJson(errorResponse);
            response.setStatus(500);
        } finally {
            leaveTypeList = null;
            ClientResponseHandler.sendResponseToClient(response, jsonResponse, logger);
        }
    }
}
