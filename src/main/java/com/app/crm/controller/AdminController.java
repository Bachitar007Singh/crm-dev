package com.app.crm.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    
@GetMapping("/calendar")
public String showform() {
	return "admin/calendar";
}
@GetMapping("/calendarpro")
public String showcalenderpro(HttpSession session, HttpServletResponse response, Model model) {
    try {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        if (session.getAttribute("adminid") != null || session.getAttribute("counselorId") != null) {
            List<Registration> leadlist;

            // Fetch the role from the session
            String role = (String) session.getAttribute("role");

            // Fetch leads based on the logged-in user's role
            if ("Employee".equals(role)) {
                // For Employee, fetch only leads assigned to them
                Integer counselorId = (Integer) session.getAttribute("counselorId");
                leadlist = rrepo.findByCounselorId(counselorId); // Fetch leads assigned to this counselor
            } else {
                // For Admin and Manager, fetch all leads
                leadlist = rrepo.findAll(); // Fetch all leads
            }

            // Sort the leadlist by registrationDate in descending order
            leadlist.sort((r1, r2) -> r2.getRegistrationDate().compareTo(r1.getRegistrationDate()));

            // Fetch all active counselors
            List<Counselor> counselors = crepo.findByActiveTrue();
            model.addAttribute("counselors", counselors); // Add counselors to the model

            model.addAttribute("leadlist", leadlist);
            return "/admin/";
        } else {
            return "redirect:/adminlogin";
        }
    } catch (Exception ex) {
        return "redirect:/adminlogin";
    }
}

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

    @GetMapping("/lead-details")
    public String showLeadDetails(@RequestParam("id") int leadId, Model model) {
        // Fetch the lead details from the repository using the leadId
        Registration lead = rrepo.findById(leadId)
                .orElseThrow(() -> new EntityNotFoundException("Lead not found with id: " + leadId));

        // Add the lead details to the model
        model.addAttribute("lead", lead);

        // Return the view name (e.g., lead-details.html)
        return "admin/lead-details";
    }
    // Import Date

    @PostMapping("/update-lead-stage")
    @ResponseBody
    public ResponseEntity<String> updateLeadStage(@RequestBody Map<String, String> data) {
        try {
            // Check if leadId is present
            if (data.get("leadId") == null) {
                return ResponseEntity.badRequest().body("Lead ID is required");
            }

            int leadId = Integer.parseInt(data.get("leadId"));
            String leadStage = data.get("leadStage");
            String notes = data.get("notes");
            String followupDateStr = data.get("followupDate");
            String followupEndDateStr = data.get("followupEndDate");

            // Fetch the lead from the database
            Registration lead = rrepo.findById(leadId)
                    .orElseThrow(() -> new EntityNotFoundException("Lead not found with id: " + leadId));

            // Convert String to LocalDate
            LocalDate followupDate = LocalDate.parse(followupDateStr);
            LocalDate followupEndDate = LocalDate.parse(followupEndDateStr);

            // Convert LocalDate to Date
            Date followupDateAsDate = Date.from(followupDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date followupEndDateAsDate = Date.from(followupEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Update the lead details
            lead.setLeadStage(leadStage);
            lead.setLeadRemark(notes); // Assuming 'remark' is used for notes
            lead.setFollowupDate(followupDateAsDate);
            lead.setFollowupEndDate(followupEndDateAsDate);
            rrepo.save(lead);

            return ResponseEntity.ok("Lead stage updated successfully");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid Lead ID");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
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
    public String showCounselorPage(HttpSession session, Model model) {
        // Check if the user is logged in and has the required role
        String role = (String) session.getAttribute("role");
        if (role == null || (!role.equals("Admin") && !role.equals("Manager"))) {
            return "redirect:/adminlogin"; // Redirect unauthorized users
        }

        // Fetch counselors based on the role
        List<Counselor> activeCounselors;
        if ("Admin".equals(role)) {
            activeCounselors = crepo.findByActiveTrue(); // Admin can see all counselors
        } else {
            activeCounselors = crepo.findByRoleAndActiveTrue("Employee"); // Manager can only see Employees
        }

        model.addAttribute("counselors", activeCounselors);
        model.addAttribute("counselorDto", new CounselorDto());
        model.addAttribute("userRole", role); // Pass the user's role to the view
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
    public String deactivateCounselor(@PathVariable int id, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"Admin".equals(role)) {
            return "redirect:/adminlogin"; // Only Admin can deactivate counselors
        }
        crepo.deactivateCounselor(id);
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

                // Fetch the role from the session
                String role = (String) session.getAttribute("role");

                // Fetch leads based on the logged-in user's role
                if ("Employee".equals(role)) {
                    // For Employee, fetch only leads assigned to them
                    Integer counselorId = (Integer) session.getAttribute("counselorId");
                    leadlist = rrepo.findByCounselorId(counselorId); // Fetch leads assigned to this counselor
                } else {
                    // For Admin and Manager, fetch all leads
                    leadlist = rrepo.findAll(); // Fetch all leads
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