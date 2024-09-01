package com.wavemaker.employee.login;

import com.google.gson.Gson;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.service.UserCookieService;
import com.wavemaker.employee.service.impl.UserCookieServiceImpl;
import com.wavemaker.employee.util.CookieHandler;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebFilter("/employee/*")
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static UserCookieService userCookieService;
    private static Gson gson = null;

    @Override
    public void init(FilterConfig filterConfig) {
        try {
            userCookieService = new UserCookieServiceImpl();
            gson = new Gson();
        } catch (SQLException e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String jsonResponse = null;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String cookieValue = CookieHandler.getCookieValueByCookieName("my_auth_cookie", httpServletRequest);
        UserEntity userEntity = null;
        HttpSession session = httpServletRequest.getSession(true);
        try {
            userEntity = userCookieService.getUserEntityByCookieValue(cookieValue);
            if (cookieValue != null && userEntity != null) {
                session.setAttribute("my_user", userEntity);
                logger.info("Authentication Successfully before proceeding chain.doFilter()");
                chain.doFilter(request, response);
                logger.info("After chain.doFilter(), Code for Post Processing");
            } else {
                logger.error("Authentication failed. Redirecting to login.");
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse = gson.toJson(new ErrorResponse("Authentication required. Redirecting to login.", HttpServletResponse.SC_UNAUTHORIZED));
                sendResponse(httpServletResponse, jsonResponse);
            }
        } catch (ServerUnavilableException | ServletException | IOException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500);
            jsonResponse = gson.toJson(errorResponse);
        } finally {
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
