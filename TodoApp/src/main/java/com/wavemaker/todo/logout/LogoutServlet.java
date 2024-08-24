package com.wavemaker.todo.logout;

import com.wavemaker.todo.util.CookieHandler;
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
            Cookie cookie = CookieHandler.invalidateCookie(cookieName, httpServletRequest);
            httpServletResponse.addCookie(cookie);
            if (session != null) {
                session.invalidate();
                logger.info("User session invalidated successfully");
            }
        } catch (Exception e) {
            logger.error("Error invalidating user session", e);
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
}
