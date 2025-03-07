package com.app.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

	@GetMapping("/dashboard")
	public String showDashboard(HttpSession session, HttpServletResponse response, Model model) {
	    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // Prevent caching
	    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	    response.setHeader("Expires", "0"); // Proxies

	    // Check if the user is logged in
	    if (session.getAttribute("adminid") != null || session.getAttribute("counselorId") != null) {
	        // Fetch the role and name from the session
	        String role = (String) session.getAttribute("role");
	        String name = (String) session.getAttribute("name");

	        // Add role and name to the model
	        model.addAttribute("role", role);
	        model.addAttribute("name", name);

	        // Render the dynamic dashboard based on the role
	        if ("Admin".equals(role)) {
	            return "admin/adminhome"; // Admin dashboard
	        } else if ("Manager".equals(role)) {
	            return "counselor/manager_dashboard"; // Manager dashboard
	        } else if ("Employee".equals(role)) {
	            return "counselor/employee_dashboard"; // Employee dashboard
	        }
	    }

	    // If not logged in, redirect to login
	    return "redirect:/adminlogin";
	}
}