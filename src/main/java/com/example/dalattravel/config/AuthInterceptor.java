package com.example.dalattravel.config;

import com.example.dalattravel.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/admin")) {
            HttpSession session = request.getSession(false);
            User user = session != null ? (User) session.getAttribute("loggedInUser") : null;

            if (user == null) {
                response.sendRedirect("/login?error=please_login");
                return false;
            }

            if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
                response.sendRedirect("/login?error=forbidden");
                return false;
            }
        }
        return true;
    }
}
