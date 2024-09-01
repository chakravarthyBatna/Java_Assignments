package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.pojo.dto.EmployeeLeaveRequestVO;
import com.wavemaker.employee.service.MyLeaveService;
import com.wavemaker.employee.service.impl.MyLeaveServiceImpl;
import com.wavemaker.employee.util.ClientResponseHandler;
import com.wavemaker.employee.util.UserSessionHandler;
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
import java.sql.SQLException;
import java.util.List;

@WebServlet("/employee/leave")
public class MyLeavesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MyLeavesServlet.class);
    private MyLeaveService myLeaveService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) {
        try {
            gson = new Gson();
            myLeaveService = new MyLeaveServiceImpl();
        } catch (SQLException e) {
            logger.error("Exception : ", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String status = request.getParameter("status");
        List<EmployeeLeaveRequestVO> employeeLeaveRequestVOList = null;
        UserEntity userEntity = null;
        String jsonResponse = null;
        try {
            userEntity = UserSessionHandler.handleUserSessionAndReturnUserEntity(request, response, logger);

            if (status != null && !status.isEmpty()) {
                employeeLeaveRequestVOList = myLeaveService.getMyLeaveRequests(userEntity.getEmpId(), LeaveRequestStatus.valueOf(status));
                jsonResponse = gson.toJson(employeeLeaveRequestVOList);
            }
        } catch (ServerUnavilableException e) {
            logger.error("Error fetching Leave details for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown", e);
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
            jsonResponse = gson.toJson(errorResponse);
        } catch (Exception e) {
            logger.error("Server error occurred while processing GET request", e);
            ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setStatus(500);
            jsonResponse = gson.toJson(errorResponse);
        } finally {
            userEntity = null;
            employeeLeaveRequestVOList = null;
            ClientResponseHandler.sendResponseToClient(response, jsonResponse, logger);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String leaveRequestId = request.getParameter("leaveRequestId");
        LeaveRequest leaveRequest = null;
        LeaveRequest addedLeaveRequest = null;
        String jsonResponse = null;
        UserEntity userEntity = null;
        BufferedReader leaveRequestBufferedReader = null;
        try {
            userEntity = UserSessionHandler.handleUserSessionAndReturnUserEntity(request, response, logger);

            if (leaveRequestId != null) {
                boolean isSuccess = myLeaveService.cancelMyLeaveRequest(Integer.parseInt(leaveRequestId), userEntity.getEmpId());
                jsonResponse = gson.toJson(isSuccess);
            } else {
                leaveRequestBufferedReader = request.getReader();
                leaveRequest = gson.fromJson(leaveRequestBufferedReader, LeaveRequest.class);
                leaveRequest.setEmpId(userEntity.getEmpId());
                addedLeaveRequest = myLeaveService.applyForLeave(leaveRequest);
                jsonResponse = gson.toJson(addedLeaveRequest);
            }
        } catch (LeaveDaysExceededException e) {
            logger.error("Error processing Leave request for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown", e);
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 400);
            response.setStatus(400);
            jsonResponse = gson.toJson(errorResponse);
        }
        catch (ServerUnavilableException e) {
            logger.error("Error fetching Leave details for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown", e);
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
            jsonResponse = gson.toJson(errorResponse);
        } catch (Exception e) {
            logger.error("Server error occurred while processing GET request", e);
            ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setStatus(500);
            jsonResponse = gson.toJson(errorResponse);
        } finally {
            userEntity = null;
            leaveRequest = null;
            addedLeaveRequest = null;
            leaveRequestBufferedReader = null;
            ClientResponseHandler.sendResponseToClient(response, jsonResponse, logger);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LeaveRequest leaveRequest = null;
        String jsonResponse = null;
        UserEntity userEntity = null;
        BufferedReader bufferedReader = null;
        try {
            userEntity = UserSessionHandler.handleUserSessionAndReturnUserEntity(request, response, logger);

            bufferedReader = request.getReader();
            leaveRequest = gson.fromJson(bufferedReader, LeaveRequest.class);
            myLeaveService.updateMyLeaveRequest(leaveRequest);
        } catch (ServerUnavilableException e) {
            logger.error("Error fetching Leave details for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown", e);
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
            jsonResponse = gson.toJson(errorResponse);
        } catch (Exception e) {
            logger.error("Server error occurred while processing GET request", e);
            ErrorResponse errorResponse = new ErrorResponse("Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setStatus(500);
            jsonResponse = gson.toJson(errorResponse);
        } finally {
            userEntity = null;
            leaveRequest = null;
            bufferedReader = null;
            ClientResponseHandler.sendResponseToClient(response, jsonResponse, logger);

        }
    }
}
