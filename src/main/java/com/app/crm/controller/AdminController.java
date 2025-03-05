package com.app.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.crm.model.Registration;
import com.app.crm.service.RegistrationRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;




import com.app.crm.model.AdminLogin; // Import AdminLogin
import com.app.crm.service.AdminLoginRepository; // Import AdminLoginRepository


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    RegistrationRepository rrepo;
    
    @Autowired
    AdminLoginRepository adrepo; // Correct declaration and @Autowired
    // ... rest of your code ...

    @GetMapping("/adminhome")
    public String showAdminHome(HttpSession session, HttpServletResponse response, Model model) {
        try {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            if (session.getAttribute("adminid") != null) {
                long totalLeads = rrepo.count(); // Get total leads count
                model.addAttribute("totalLeads", totalLeads); // Add it to the model
                return "/admin/adminhome"; // Adjust according to your view structure
            } else {
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            return "redirect:/adminlogin";
        }
        //========================
        try {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            if (session.getAttribute("adminid") != null) {
                long totalLeads = rrepo.count();
                model.addAttribute("totalLeads", totalLeads);

                // Fetch admin details
                String adminId = (String) session.getAttribute("adminid");
                AdminLogin admin = adrepo.findById(adminId)
                                        .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

                model.addAttribute("adminName", admin.getUserid()); // Or a name field if you have it
                model.addAttribute("adminMobile", "123-456-7890"); // Replace with actual field
                model.addAttribute("adminEmail", admin.getUserid()); // Assuming userid is email
                model.addAttribute("adminDesignation", "System Admin"); // Replace with actual field
                model.addAttribute("adminDepartment", "IT"); // Replace with actual field

                return "/admin/adminhome";
            } else {
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            return "redirect:/adminlogin";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
    	session.invalidate();
    	return "redirect:/adminlogin";
    }
    
    @GetMapping("/leadmanager")
    public String showLead(HttpSession session, HttpServletResponse response,Model model) {
        try {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            if (session.getAttribute("adminid") != null) {
            	List<Registration> leadlist=rrepo.findAll();
            	model.addAttribute("leadlist", leadlist);
                return "/admin/leadmanager"; // Adjust according to your view structure
            } else {
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            return "redirect:/adminlogin";
        }
    }
}
