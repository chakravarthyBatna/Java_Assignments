package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.constant.StorageType;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.exception.employee.EmployeeFileDeletionException;
import com.wavemaker.employee.exception.employee.EmployeeFileReadException;
import com.wavemaker.employee.exception.employee.EmployeeFileUpdateException;
import com.wavemaker.employee.exception.employee.EmployeeNotFoundException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.service.AddressService;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.AddressServiceImpl;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;
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
import java.util.List;


public class EmployeeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServlet.class);
    private static EmployeeService employeeService;
    private static AddressService addressService;
    private static Gson gson = null;

    public EmployeeServlet() {
    }

    @Override
    public void init(ServletConfig config) {
        gson = new Gson();
        String storageTypeParam = config.getInitParameter("storageType");
        try {
            if (storageTypeParam != null) {
                StorageType storageType = StorageType.valueOf(storageTypeParam);
                employeeService = new EmployeeServiceImpl(storageType);
                addressService = new AddressServiceImpl(storageType);
            }
        } catch (SQLException | FileCreationException exception) {
            logger.info("Exception ; ", exception);
        }
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonEmployeeResponse = null;
        String empId = httpServletRequest.getParameter("empId");
        logger.info("Received GET request with empId: {}", empId);

        if (empId != null) {
            try {
                logger.info("Fetching employee details for empId: {}", empId);
                Employee employee = employeeService.getEmployeeById(Integer.parseInt(empId));
                jsonEmployeeResponse = gson.toJson(employee);
                logger.info("Successfully fetched employee details for emp: {}", employee);
            } catch (ServerUnavilableException | EmployeeFileReadException exception) {
                logger.error("Error fetching employee details for empId: {}", empId, exception);
                ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 500);
                jsonEmployeeResponse = gson.toJson(errorResponse);
            }
        } else {
            try {
                logger.info("Fetching all employee details");
                List<Employee> employeeList = employeeService.getAllEmployeeDetails();
                jsonEmployeeResponse = gson.toJson(employeeList);
                logger.info("Successfully fetched all employee details");
            } catch (EmployeeFileReadException | ServerUnavilableException exception) {
                logger.error("Error fetching all employee details", exception);
                ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 500);
                jsonEmployeeResponse = gson.toJson(errorResponse);
            }
        }
        sendResponse(httpServletResponse, jsonEmployeeResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;

        HttpSession userSession = httpServletRequest.getSession(true);
        String cookieValue = HttpUtil.getCookieByName("my_auth_cookie", httpServletRequest);
        if (userSession.getAttribute(cookieValue) != null) {
            logger.info("User Found to be an Admin");
            BufferedReader employeeBufferReader = null;
            Employee employee = null;
            Employee addedEmployee = null;

            try {
                logger.info("Received POST request to add a new employee");
                employeeBufferReader = httpServletRequest.getReader();
                employee = gson.fromJson(employeeBufferReader, Employee.class);
                addedEmployee = employeeService.addEmployee(employee);
                jsonResponse = gson.toJson(addedEmployee);
                logger.info("Successfully added employee: {}", addedEmployee);
            } catch (ServerUnavilableException | EmployeeFileReadException exception) {
                logger.error("Error adding employee", exception);
                ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 500);
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(500);
            } catch (IOException e) {
                logger.error("Error parsing request body", e);
                ErrorResponse errorResponse = new ErrorResponse("Invalid input format", 400);
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(400);
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
                try {
                    closeBufferReader(employeeBufferReader);
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
    protected void doPut(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String jsonResponse = null;
        HttpSession userSession = httpServletRequest.getSession(true);
        String cookieValue = HttpUtil.getCookieByName("my_auth_cookie", httpServletRequest);
        if (userSession.getAttribute(cookieValue) != null) {
            Employee employee = null;
            Employee updatedEmployee = null;
            BufferedReader employeeBufferReader = null;

            try {
                logger.info("Received PUT request to update employee details");
                employeeBufferReader = httpServletRequest.getReader();
                employee = gson.fromJson(employeeBufferReader, Employee.class);
                updatedEmployee = employeeService.updateEmployee(employee);
                jsonResponse = gson.toJson(updatedEmployee);

                logger.info("Successfully updated employee: {}", updatedEmployee);
            } catch (EmployeeFileUpdateException | EmployeeNotFoundException exception) {
                logger.error("Error updating employee", exception);
                ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 404); // 404 for Not Found or appropriate status code
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(404); // Not Found
            } catch (ServerUnavilableException exception) {
                logger.error("Server is unavailable while updating employee", exception);
                ErrorResponse errorResponse = new ErrorResponse("Server is unavailable", 503); // 503 for Service Unavailable
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(503);
            } catch (IOException e) {
                logger.error("Error parsing request body", e);
                ErrorResponse errorResponse = new ErrorResponse("Invalid input format", 400);
                jsonResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(400);
            } finally {
                sendResponse(httpServletResponse, jsonResponse);
                try {
                    closeBufferReader(employeeBufferReader);
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
        String jsonEmployeeResponse = null;
        HttpSession userSession = httpServletRequest.getSession(true);
        String cookieValue = HttpUtil.getCookieByName("my_auth_cookie", httpServletRequest);
        if (userSession.getAttribute(cookieValue) != null) {
            Employee deletedEmployee = null;
            String empId = httpServletRequest.getParameter("empId");
            logger.info("Received DELETE request with empId: {}", empId);

            try {
                if (empId == null || empId.isEmpty()) {
                    logger.warn("No empId provided in DELETE request");
                    ErrorResponse errorResponse = new ErrorResponse("empId parameter is required", 400);
                    jsonEmployeeResponse = gson.toJson(errorResponse);
                    httpServletResponse.setStatus(400); // Bad Request
                } else {
                    try {
                        int id = Integer.parseInt(empId);
                        deletedEmployee = employeeService.deleteEmployee(id);
                        logger.info("Successfully deleted employee: {}", deletedEmployee);
                        jsonEmployeeResponse = gson.toJson(deletedEmployee);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid empId format: {}", empId, e);
                        ErrorResponse errorResponse = new ErrorResponse("Invalid empId format", 400);
                        jsonEmployeeResponse = gson.toJson(errorResponse);
                        httpServletResponse.setStatus(400); // Bad Request
                    } catch (EmployeeFileDeletionException | EmployeeNotFoundException e) {
                        logger.error("Error deleting employee with empId: {}", empId, e);
                        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 404); // Not Found
                        jsonEmployeeResponse = gson.toJson(errorResponse);
                        httpServletResponse.setStatus(404); // Not Found
                    } catch (ServerUnavilableException | EmployeeFileUpdateException | EmployeeFileReadException e) {
                        logger.error("Server error while deleting employee with empId: {}", empId, e);
                        ErrorResponse errorResponse = new ErrorResponse("Server error", 503); // Service Unavailable
                        jsonEmployeeResponse = gson.toJson(errorResponse);
                        httpServletResponse.setStatus(503); // Service Unavailable
                    }
                }
            } catch (Exception e) {
                logger.error("Unexpected error during DELETE operation", e);
                ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500); // Internal Server Error
                jsonEmployeeResponse = gson.toJson(errorResponse);
                httpServletResponse.setStatus(500); // Internal Server Error
                sendResponse(httpServletResponse, jsonEmployeeResponse);
            } finally {
                sendResponse(httpServletResponse, jsonEmployeeResponse);
            }
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Invalid User Found, Access Denied", HttpServletResponse.SC_BAD_REQUEST);
            jsonEmployeeResponse = gson.toJson(errorResponse);
            httpServletResponse.setStatus(400);
            sendResponse(httpServletResponse, jsonEmployeeResponse);
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