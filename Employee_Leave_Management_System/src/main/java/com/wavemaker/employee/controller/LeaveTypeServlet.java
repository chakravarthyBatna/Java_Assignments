package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.LeaveType;
import com.wavemaker.employee.service.LeaveTypeService;
import com.wavemaker.employee.service.impl.LeaveTypeServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/leavetypes")
public class LeaveTypeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LeaveTypeServlet.class);
    private static LeaveTypeService leaveTypeService;
    private Gson gson = null;
    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            leaveTypeService = new LeaveTypeServiceImpl();
            gson = new Gson();
        } catch (SQLException e) {
            logger.error("Exception : ",e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<LeaveType> leaveTypeList = null;
        String jsonResponse = null;
        try {
            leaveTypeList = leaveTypeService.getAllLeaveTypes();
            jsonResponse = gson.toJson(leaveTypeList);
            sendResponse(response, jsonResponse);
        } catch (ServerUnavilableException e) {
            ErrorResponse errorResponse = new ErrorResponse("Error occurred while retrieving leave types", 500);
            jsonResponse = gson.toJson(errorResponse);
            response.setStatus(500);
            sendResponse(response, jsonResponse);
        }
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
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpServletResponse.setStatus(500);
            jsonResponse = gson.toJson(errorResponse);
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
}
