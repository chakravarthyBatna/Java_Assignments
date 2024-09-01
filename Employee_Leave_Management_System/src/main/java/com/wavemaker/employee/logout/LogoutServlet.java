package com.wavemaker.employee.logout;

import com.wavemaker.employee.service.UserCookieService;
import com.wavemaker.employee.service.impl.UserCookieServiceImpl;
import com.wavemaker.employee.util.CookieHandler;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    private UserCookieService userCookieService = null;

    @Override
    public void init(ServletConfig config)  {
        try {
            userCookieService = new UserCookieServiceImpl();
        } catch (SQLException e) {
            logger.error("Exception", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        String cookieName = "my_auth_cookie";
        String cookieValue = null;
        try {
            // Invalidate the session if it exists
            if (session != null) {
                session.invalidate();
                logger.info("User session invalidated successfully");
            }
            cookieValue = CookieHandler.getCookieValueByCookieName(cookieName, request);
            if (cookieValue!= null) {
                userCookieService.deleteUserCookie(cookieValue);
                logger.info("User cookie deleted successfully");
            }
            // Invalidate the authentication cookie
            Cookie cookie = CookieHandler.invalidateCookie(cookieName, request);
            response.addCookie(cookie);

            // Redirect to login page or home page after logout
            response.sendRedirect("Login.jsp");
        } catch (Exception e) {
            logger.error("Error invalidating user session", e);
        }
    }
}
