package com.app.crm.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.crm.dto.CounselorDto;
import com.app.crm.model.Counselor;
import com.app.crm.model.Registration;
import com.app.crm.service.RegistrationRepository;
import com.app.crm.util.SessionRegistry;
import com.app.crm.service.CounselorRepository; // Import CounselorRepository

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    RegistrationRepository rrepo;

    @Autowired
    CounselorRepository crepo; // Autowire CounselorRepository

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
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        Object adminIdObj = session.getAttribute("adminid");
        Object counselorIdObj = session.getAttribute("counselorId");
        String username = null;

        if (adminIdObj != null) {
            username = adminIdObj instanceof Integer ? adminIdObj.toString() : (String) adminIdObj;
        } else if (counselorIdObj != null) {
            username = counselorIdObj instanceof Integer ? counselorIdObj.toString() : (String) counselorIdObj;
        }

        if (username != null) {
            SessionRegistry.removeSession(username); // Remove the session from the registry
        }
        session.invalidate(); // Invalidate the session
        return "redirect:/adminlogin";
    }

    @GetMapping("/counselor")
    public String showCounselorPage(Model model) {
        List<Counselor> activeCounselors = crepo.findByActiveTrue(); // Fetch only active counselors
        model.addAttribute("counselors", activeCounselors);
        model.addAttribute("counselorDto", new CounselorDto());
        return "admin/counselor";
    }

    @PostMapping("/saveCounselor")
    public String saveCounselor(@ModelAttribute CounselorDto dto) {
        Counselor counselor;
        if (dto.getId() != null) {
            // Update existing counselor
            counselor = crepo.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Counselor not found"));
        } else {
            // Create new counselor
            counselor = new Counselor();
        }
        counselor.setName(dto.getName());
        counselor.setEmail(dto.getEmail());
        counselor.setPassword(dto.getPassword());
        counselor.setPhoneNumber(dto.getPhoneNumber());
        counselor.setRole(dto.getRole());
        counselor.setActive(true); // Ensure the counselor is active
        crepo.save(counselor);
        return "redirect:/admin/counselor";
    }
    
    @GetMapping("/deactivateCounselor/{id}")
    public String deactivateCounselor(@PathVariable int id) {
        crepo.deactivateCounselor(id); // Deactivate counselor
        return "redirect:/admin/counselor";
    }

    // Fetch counselor data for editing
    @GetMapping("/getCounselor/{id}")
    @ResponseBody
    public Counselor getCounselor(@PathVariable int id) {
        return crepo.findById(id).orElseThrow(() -> new RuntimeException("Counselor not found"));
    }

    @GetMapping("/user_dashboard")
    public String userDashboard(Model model) {
        // Add any required attributes to the model
        model.addAttribute("pageTitle", "User Dashboard");
        // Return the view name for the user dashboard
        return "admin/Dashboard/user_dashboard";
    }

    @GetMapping("/Student_QI")
    public String studentqualityindex(Model model) {
        // Add any required attributes to the model
        model.addAttribute("pageTitle", "User Dashboard");
        // Return the view name for the user dashboard
        return "admin/Dashboard/Student_QI";
    }

    @GetMapping("/leadmanager")
    public String showLead(HttpSession session, HttpServletResponse response, Model model) {
        try {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            if (session.getAttribute("adminid") != null || session.getAttribute("counselorId") != null) {
                List<Registration> leadlist;

                // Fetch leads based on the logged-in user's role
                if (session.getAttribute("counselorId") != null) {
                    Integer counselorId = (Integer) session.getAttribute("counselorId");
                    leadlist = rrepo.findByCounselorId(counselorId); // Fetch leads assigned to this counselor
                } else {
                    leadlist = rrepo.findAll(); // Fetch all leads for admin
                }

                // Sort the leadlist by registrationDate in descending order
                leadlist.sort((r1, r2) -> r2.getRegistrationDate().compareTo(r1.getRegistrationDate()));

                // Fetch all active counselors
                List<Counselor> counselors = crepo.findByActiveTrue();
                model.addAttribute("counselors", counselors); // Add counselors to the model

                model.addAttribute("leadlist", leadlist);
                return "/admin/leadmanager";
            } else {
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            return "redirect:/adminlogin";
        }
    }
}