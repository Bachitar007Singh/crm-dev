package com.app.crm.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class SessionValidationFilter implements jakarta.servlet.Filter {

	 @Override
	    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	            throws IOException, ServletException {
	        HttpServletRequest httpRequest = (HttpServletRequest) request;
	        HttpServletResponse httpResponse = (HttpServletResponse) response;
	        HttpSession session = httpRequest.getSession(false);

	        // Check if the session is valid
	        if (session == null || session.getAttribute("adminid") == null && session.getAttribute("counselorId") == null) {
	            // Redirect to login page if the session is invalid
	            httpResponse.sendRedirect(httpRequest.getContextPath() + "/adminlogin");
	            return;
	        }

	        chain.doFilter(request, response);
	    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic (if needed)
    }

    @Override
    public void destroy() {
        // Cleanup logic (if needed)
    }
}