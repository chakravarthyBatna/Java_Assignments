package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.pojo.dto.LeaveRequestVO;
import com.wavemaker.employee.service.MyTeamLeaveService;
import com.wavemaker.employee.service.impl.MyTeamLeaveServiceImpl;
import com.wavemaker.employee.util.ClientResponseHandler;
import com.wavemaker.employee.util.UserSessionHandler;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

@WebServlet("/employee/my-team-leave")
public class MyTeamLeavesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MyLeavesServlet.class);
    private Gson gson;
    private MyTeamLeaveService myTeamLeaveService;

    @Override
    public void init(ServletConfig config) {
        try {
            myTeamLeaveService = new MyTeamLeaveServiceImpl();
            gson = new Gson();
        } catch (SQLException sqlException) {
            logger.error("Exception : ", sqlException);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String status = request.getParameter("status");
        List<LeaveRequestVO> leaveRequestList = null;
        UserEntity userEntity = null;
        String jsonResponse = null;
        try {
            userEntity = UserSessionHandler.handleUserSessionAndReturnUserEntity(request, response, logger);

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
            status = null;
            leaveRequestList = null;
            userEntity = null;
            ClientResponseHandler.sendResponseToClient(response, jsonResponse, logger);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String leaveRequestId = request.getParameter("leaveRequestId");
        String approveOrReject = request.getParameter("approveOrReject");
        String jsonResponse = null;
        UserEntity userEntity = null;
        try {
            userEntity = UserSessionHandler.handleUserSessionAndReturnUserEntity(request, response, logger);

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
            userEntity = null;
            approveOrReject = null;
            leaveRequestId = null;
            ClientResponseHandler.sendResponseToClient(response, jsonResponse, logger);
        }
    }
}
