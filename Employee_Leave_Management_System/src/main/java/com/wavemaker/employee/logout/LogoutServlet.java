package com.wavemaker.employee.logout;

import com.wavemaker.employee.util.CookieHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        HttpSession session = httpServletRequest.getSession(false);
        String cookieName = "my_auth_cookie";

        try {
            // Invalidate the session if it exists
            if (session != null) {
                session.invalidate();
                logger.info("User session invalidated successfully");
            }

            // Invalidate the authentication cookie
            Cookie cookie = CookieHandler.invalidateCookie(cookieName, httpServletRequest);
            httpServletResponse.addCookie(cookie);

            // Redirect to login page or home page after logout
            httpServletResponse.sendRedirect("Login.html");
        } catch (Exception e) {
            logger.error("Error invalidating user session", e);
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
}
