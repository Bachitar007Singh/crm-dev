package com.app.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.crm.service.RegistrationRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {
	
	 @Autowired
	    RegistrationRepository rrepo;

	 @GetMapping("/dashboard")
	 public String showDashboard(HttpSession session, Model model) {
	     System.out.println("Session ID: " + session.getId());
	     System.out.println("Admin ID: " + session.getAttribute("adminid"));
	     System.out.println("Counselor ID: " + session.getAttribute("counselorId"));
	     System.out.println("Role: " + session.getAttribute("role"));
	     System.out.println("Name: " + session.getAttribute("name"));

	     if (session.getAttribute("adminid") != null || session.getAttribute("counselorId") != null) {
	         String role = (String) session.getAttribute("role");
	         String name = (String) session.getAttribute("name");

	         long totalLeads = rrepo.count();
	         model.addAttribute("role", role);
	         model.addAttribute("name", name);
	         model.addAttribute("totalLeads", totalLeads);

	         if ("Admin".equals(role)) {
	             return "admin/adminhome";
	         } else if ("Manager".equals(role)) {
	             return "counselor/manager_dashboard"; // Ensure this view exists
	         } else if ("Employee".equals(role)) {
	             return "counselor/employee_dashboard"; // Ensure this view exists
	         }
	     }

	     return "redirect:/adminlogin";
	 }
}