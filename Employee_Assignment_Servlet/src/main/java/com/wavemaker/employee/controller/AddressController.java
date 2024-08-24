package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.constant.StorageType;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.service.AddressService;
import com.wavemaker.employee.service.impl.AddressServiceImpl;
import com.wavemaker.employee.util.HttpUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class AddressController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    private static AddressService addressService;
    private static Gson gson = null;

    @Override
    public void init(ServletConfig config) {
        gson = new Gson();
        String storageTypeParam = config.getInitParameter("storageType");
        try {
            if (storageTypeParam != null) {
                StorageType storageType = StorageType.valueOf(storageTypeParam);
                addressService = new AddressServiceImpl(storageType);
            }
        } catch (SQLException | FileCreationException exception) {
            logger.info("Exception ; ", exception);
        }
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonEmployeeResponse = null;
        Address address = null;
        String empId = httpServletRequest.getParameter("empId");
        logger.info("Received GET request with empId: {}", empId);

        if (empId != null && !empId.isEmpty()) {
            try {
                logger.info("Fetching address details for empId: {}", empId);
                address = addressService.getAddressByEmpId(Integer.parseInt(empId));
                jsonEmployeeResponse = gson.toJson(address);
                logger.info("Successfully fetched address details for empId: {}", empId);
            } catch (NumberFormatException e) {
                logger.error("Invalid empId format: {}", empId, e);
                ErrorResponse errorResponse = new ErrorResponse("Invalid empId format", 400); // Bad Request
                jsonEmployeeResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(400); // Bad Request
            } catch (ServerUnavilableException e) {
                logger.error("Error fetching address details for empId: {}", empId, e);
                ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500); // Internal Server Error
                jsonEmployeeResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(500); // Internal Server Error
            } catch (Exception e) {
                logger.error("Unexpected error while fetching address for empId: {}", empId, e);
                ErrorResponse errorResponse = new ErrorResponse("Unexpected error", 500); // Internal Server Error
                jsonEmployeeResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(500);
            }
        } else {
            logger.warn("No empId provided in GET request");
            ErrorResponse errorResponse = new ErrorResponse("empId parameter is required", 400); // Bad Request
            jsonEmployeeResponse = gson.toJson(errorResponse);
            httpServletResponse.setStatus(400); // Bad Request
        }
        sendResponse(httpServletResponse, jsonEmployeeResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        HttpSession userSession = httpServletRequest.getSession(true);
        String cookieValue = HttpUtil.getCookieByName("my_auth_cookie", httpServletRequest);
        if (userSession.getAttribute(cookieValue) != null) {

            BufferedReader addressBufferReader = null;
            Address address = null;
            logger.info("Received POST request for Address");
            try {
                addressBufferReader = httpServletRequest.getReader();
                address = gson.fromJson(addressBufferReader, Address.class);
                Address addedAddress = addressService.addAddress(address);
                jsonResponse = gson.toJson(addedAddress);
                logger.info("Successfully added address: {}", addedAddress);
            } catch (IOException e) {
                logger.error("Error reading or processing request data", e);
                ErrorResponse errorResponse = new ErrorResponse("Internal server error", 500); // Internal Server Error
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(500);
            } catch (Exception e) {
                logger.error("Unexpected error while processing address data", e);
                ErrorResponse errorResponse = new ErrorResponse("Unexpected error", 500); // Internal Server Error
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(500);
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
            }
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse = gson.toJson(errorResponse);
            httpServletResponse.setStatus(400);
            sendResponse(httpServletResponse, jsonResponse);
        }
    }

    @Override
    protected void doPut(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        HttpSession userSession = httpServletRequest.getSession(true);
        String cookieValue = HttpUtil.getCookieByName("my_auth_cookie", httpServletRequest);
        if (userSession.getAttribute(cookieValue) != null) {
            BufferedReader addressBufferReader = null;
            PrintWriter printWriter = null;
            Address address = null;
            logger.info("Received PUT request to update address");
            try {
                addressBufferReader = httpServletRequest.getReader();
                address = gson.fromJson(addressBufferReader, Address.class);
                Address updatedAddress = addressService.updateAddress(address);
                jsonResponse = gson.toJson(updatedAddress);
                logger.info("Successfully updated address: {}", updatedAddress);
            } catch (ServerUnavilableException e) {
                logger.error("Server error while updating address", e);
                ErrorResponse errorResponse = new ErrorResponse("Internal server error", 500); // Internal Server Error
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(500);
            } catch (Exception e) {
                logger.error("Unexpected error while processing address update", e);
                ErrorResponse errorResponse = new ErrorResponse("Unexpected error", 500); // Internal Server Error
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(500);
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
                try {
                    closeBufferReader(addressBufferReader);
                } catch (IOException e) {
                    logger.error("Error While closing the BufferReader");
                    ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
                    jsonResponse = gson.toJson(errorResponse);
                    httpServletResponse.setStatus(500);
                    sendResponse(httpServletResponse, jsonResponse);
                }
            }
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse = gson.toJson(errorResponse);
            httpServletResponse.setStatus(400);
            sendResponse(httpServletResponse, jsonResponse);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        HttpSession userSession = httpServletRequest.getSession(true);
        String cookieValue = HttpUtil.getCookieByName("my_auth_cookie", httpServletRequest);
        if (userSession.getAttribute(cookieValue) != null) {
            String empId = httpServletRequest.getParameter("empId");
            logger.info("Received DELETE request to delete address for empId: {}", empId);
            try {
                Address addedAddress = addressService.deleteAddressByEmpId(Integer.parseInt(empId));
                logger.info("Successfully deleted address for empId: {}", empId);
                jsonResponse = gson.toJson(addedAddress);
                httpServletResponse.setStatus(200); // OK
            } catch (ServerUnavilableException e) {
                logger.error("Server error while deleting address for empId: {}", empId, e);
                ErrorResponse errorResponse = new ErrorResponse("Internal server error", 500); // Internal Server Error
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(500);
            } catch (NumberFormatException e) {
                logger.error("Invalid empId format: {}", empId, e);
                ErrorResponse errorResponse = new ErrorResponse("Invalid empId format", 400); // Bad Request
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(400); // Bad Request
            } catch (Exception e) {
                logger.error("Unexpected error while deleting address for empId: {}", empId, e);
                ErrorResponse errorResponse = new ErrorResponse("Unexpected error", 500); // Internal Server Error
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(500); // Internal Server Error
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
            }
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse = gson.toJson(errorResponse);
            httpServletResponse.setStatus(400);
            sendResponse(httpServletResponse, jsonResponse);
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
