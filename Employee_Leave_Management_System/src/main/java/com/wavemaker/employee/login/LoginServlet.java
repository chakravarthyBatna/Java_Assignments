package com.wavemaker.employee.login;

import com.google.gson.Gson;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.pojo.dto.EmployeeVO;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.UserCookieService;
import com.wavemaker.employee.service.UserEntityService;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;
import com.wavemaker.employee.service.impl.UserCookieServiceImpl;
import com.wavemaker.employee.service.impl.UserEntityServiceImpl;
import com.wavemaker.employee.util.ClientResponseHandler;
import com.wavemaker.employee.util.CookieHandler;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private static UserEntityService userEntityService = null;
    private static Gson gson = null;
    private static UserCookieService userCookieService = null;
    private EmployeeService employeeService = null;

    @Override
    public void init(ServletConfig config) {
        try {
            gson = new Gson();
            userEntityService = new UserEntityServiceImpl();
            userCookieService = new UserCookieServiceImpl();
            employeeService = new EmployeeServiceImpl();
        } catch (SQLException e) {
            logger.error("Exception : ", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Login Request Received");
        ErrorResponse errorResponse = null;
        String jsonResponse = null;
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        try {
            logger.info(" Email : {}", userEntity.getEmail());
            UserEntity authenticatedUser = userEntityService.authenticateUser(userEntity);
            if (authenticatedUser != null) {
                String cookieValue = UUID.randomUUID().toString(); //generating random cookie
                String cookieName = "my_auth_cookie";  //set default cookie value
                Cookie cookie = new Cookie(cookieName, cookieValue); //creating a cookie with name,value;

                userCookieService.addCookie(cookieValue, authenticatedUser.getEmpId());

                logger.info("user cookie added successfully email : {} and cookie : {}", authenticatedUser.getEmail(), cookieValue);
                response.addCookie(cookie); //added cookie to response

                logger.info("User Cookie added Successfully to browser : {}", cookie);
                response.sendRedirect("index.html");
            } else {
                request.setAttribute("errorMessage", "Invalid Username or Password");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                logger.error("Invalid User Found: Username: {} and User Password: {}", email, password);
            }
        } catch (Exception e) {
            errorResponse = new ErrorResponse("Error - While logging : " + e.getMessage(), 500);
            jsonResponse = gson.toJson(errorResponse);
            logger.error("Error Occurred while trying to Login ", e);
        } finally {
            ClientResponseHandler.sendResponseToClient(response,jsonResponse,logger);
        }

    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException {
        ErrorResponse errorResponse = null;
        EmployeeVO employeeVO = null;
        String jsonResponse = null;
        String cookieValue = CookieHandler.getCookieValueByCookieName("my_auth_cookie", httpServletRequest);
        UserEntity userEntity = null;
        try {
            userEntity = userCookieService.getUserEntityByCookieValue(cookieValue);
            if (userEntity == null) {
                errorResponse = new ErrorResponse("Invalid User Found",HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse = gson.toJson(errorResponse);
                ClientResponseHandler.sendResponseToClient(response,jsonResponse,logger);
            }
            employeeVO = employeeService.getEmployeeById(userEntity.getEmpId());
            jsonResponse = gson.toJson(employeeVO);

        } catch (ServerUnavilableException e) {
            errorResponse = new ErrorResponse("Error - User not found in the system : " + e.getMessage(), 401);
            jsonResponse = gson.toJson(errorResponse);
            logger.error("Error Occurred while trying to get User from cookie", e);
        } finally {
            ClientResponseHandler.sendResponseToClient(response,jsonResponse,logger);
        }
    }

}
