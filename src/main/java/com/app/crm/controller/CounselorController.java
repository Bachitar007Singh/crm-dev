package com.app.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/counselor")
public class CounselorController {

    @GetMapping("/manager_dashboard")
    public String showManagerDashboard(HttpSession session, Model model) {
        if (session.getAttribute("counselorId") != null && "Manager".equals(session.getAttribute("counselorRole"))) {
            // Add any required attributes to the model
            return "counselor/manager_dashboard";
        } else {
            return "redirect:/adminlogin";
        }
    }

    @GetMapping("/employee_dashboard")
    public String showEmployeeDashboard(HttpSession session, Model model) {
        if (session.getAttribute("counselorId") != null && "Employee".equals(session.getAttribute("counselorRole"))) {
            // Add any required attributes to the model
            return "counselor/employee_dashboard";
        } else {
            return "redirect:/adminlogin";
        }
    }
}