package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.dto.LeaveRequestVO;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.service.MyTeamLeaveService;
import com.wavemaker.employee.service.impl.MyTeamLeaveServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
@WebServlet("/employee/my-team-leave")
public class MyTeamLeavesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MyLeavesServlet.class);
    private Gson gson;
    private MyTeamLeaveService myTeamLeaveService;

    @Override
    public void init(ServletConfig config) {
        myTeamLeaveService = new MyTeamLeaveServiceImpl();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String status = request.getParameter("status");
        List<LeaveRequestVO> leaveRequestList = null;
        UserEntity userEntity = null;
        HttpSession session = null;
        String jsonResponse = null;
        try {
            session = request.getSession(true);
            logger.info("Session retrieved: sessionId={}", session.getId());
            userEntity = (UserEntity) session.getAttribute("my_user");
            if (userEntity == null) {
                logger.warn("Invalid user found in session: sessionId={}", session.getId());
                ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse = gson.toJson(errorResponse);
                response.setStatus(400);
                sendResponse(response, jsonResponse);
            }
            if (status != null) {
                leaveRequestList = myTeamLeaveService.getMyTeamLeaveRequests(userEntity.getEmpId(), status);
                jsonResponse = gson.toJson(leaveRequestList);
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
            sendResponse(response, jsonResponse);
            logger.info("Response sent with status: {}", response.getStatus());
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String leaveRequestId = request.getParameter("leaveRequestId");
        String approveOrReject = request.getParameter("approveOrReject");
        LeaveRequest leaveRequest = null;
        LeaveRequest addedLeaveRequest = null;
        String jsonResponse = null;
        UserEntity userEntity = null;
        HttpSession session = null;
        BufferedReader leaveRequestBufferedReader = null;
        try {
            session = request.getSession(true);
            logger.info("Session retrieved: sessionId={}", session.getId());
            userEntity = (UserEntity) session.getAttribute("my_user");

            if (userEntity == null) {
                logger.warn("Invalid user found in session: sessionId={}", session.getId());
                ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse = gson.toJson(errorResponse);
                response.setStatus(400);
                sendResponse(response, jsonResponse);
                return;
            }
            if (leaveRequestId != null && approveOrReject != null) {
                boolean isSuccess = myTeamLeaveService.approveOrRejectTeamLeaveRequest(Integer.parseInt(leaveRequestId), userEntity.getEmpId(), LeaveRequestStatus.valueOf(approveOrReject));
                jsonResponse = gson.toJson(isSuccess);
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
            sendResponse(response, jsonResponse);
            logger.info("Response sent with status: {}", response.getStatus());
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
