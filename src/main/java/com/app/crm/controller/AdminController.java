package com.app.crm.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.app.crm.dto.AdminLoginDto;
import com.app.crm.dto.CounselorDto;
import com.app.crm.model.AdminLogin;
import com.app.crm.model.Counselor;
import com.app.crm.model.Registration;
import com.app.crm.service.RegistrationRepository;
import com.app.crm.service.CounselorRepository;
import com.app.crm.service.AdminLoginRepository; // Add this import

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    RegistrationRepository rrepo;

    @Autowired
    CounselorRepository crepo;

    @Autowired
    AdminLoginRepository adminLoginRepository; // Add this autowired field

    @GetMapping("/adminhome")
    public String showAdminHome(HttpSession session, HttpServletResponse response, Model model) {
        try {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            if (session.getAttribute("adminid") != null) {
                long totalLeads = rrepo.count();
                model.addAttribute("totalLeads", totalLeads);
                return "admin/adminDashboard"; // Ensure this matches the template path
            } else {
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            return "redirect:/adminlogin";
        }
    }

    @PostMapping("/login")
    public String login(@ModelAttribute AdminLoginDto dto, HttpSession session, Model model) {
        try {
            // Debug: Print the received user ID and password
            System.out.println("Login attempt - User ID: " + dto.getUserid());
            System.out.println("Login attempt - Password: " + dto.getPassword());

            // Fetch admin from the database
            AdminLogin admin = adminLoginRepository.findByUserid(dto.getUserid());
            if (admin == null) {
                System.out.println("Admin not found for User ID: " + dto.getUserid());
                model.addAttribute("error", "Invalid credentials");
                return "redirect:/adminlogin";
            }

            // Debug: Print the admin details fetched from the database
            System.out.println("Admin found - User ID: " + admin.getUserid());
            System.out.println("Admin found - Password: " + admin.getPassword());
            System.out.println("Admin found - Role: " + admin.getRole());

            // Compare passwords (plain text comparison for now)
            if (admin.getPassword().equals(dto.getPassword())) {
                // Set session attributes
                session.setAttribute("adminid", admin.getUserid());
                session.setAttribute("role", admin.getRole());
                if ("Employee".equals(admin.getRole())) {
                    session.setAttribute("counselorId", admin.getUserid());
                }

                // Debug: Print session attributes after setting
                System.out.println("Session - adminid: " + session.getAttribute("adminid"));
                System.out.println("Session - role: " + session.getAttribute("role"));
                System.out.println("Session - counselorId: " + session.getAttribute("counselorId"));

                return "redirect:/admin/adminhome";
            } else {
                System.out.println("Password mismatch for User ID: " + dto.getUserid());
                model.addAttribute("error", "Invalid credentials");
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("error", "An error occurred during login");
            return "redirect:/adminlogin";
        }
    }

   

    @GetMapping("/lead-details")
    public String showLeadDetails(@RequestParam("id") Long leadId, Model model) {
        // Fetch the lead details from the repository using the leadId
        Registration lead = rrepo.findById(leadId)
                .orElseThrow(() -> new EntityNotFoundException("Lead not found with id: " + leadId));

        // Fetch all active counselors
        List<Counselor> counselors = crepo.findByActiveTrue();

        // Add the lead details and counselors to the model
        model.addAttribute("lead", lead);
        model.addAttribute("counselors", counselors);

        // Return the view name (e.g., lead-details.html)
        return "admin/lead-details";
    }

    @PostMapping("/update-lead-stage")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> updateLeadStage(@RequestBody Map<String, String> data) {
        try {
            // Log the received data
            System.out.println("Update lead stage endpoint called with data: " + data);

            // Parse the data
            Long leadId = (long) Integer.parseInt(data.get("leadId"));
            String leadStage = data.get("leadStage");
            String counselorName = data.get("counselorName");
            String followupDateStr = data.get("followupDate"); // Get followupDate as string
            String leadRemark = data.get("leadRemark");

            // Log the parsed data
            System.out.println("Lead ID: " + leadId);
            System.out.println("Lead Stage: " + leadStage);
            System.out.println("Counselor Name: " + counselorName);
            System.out.println("Followup Date: " + followupDateStr);
            System.out.println("Lead Remark: " + leadRemark);

            // Fetch the lead from the database
            Registration lead = rrepo.findById(leadId)
                    .orElseThrow(() -> new EntityNotFoundException("Lead not found with id: " + leadId));

            // Update the lead details
            lead.setLeadStage(leadStage);
            lead.setCounselorName(counselorName);
            lead.setLeadRemark(leadRemark);

            // Parse followupDate from String to LocalDate and then to Date
            if (followupDateStr != null && !followupDateStr.isEmpty()) {
                LocalDate followupLocalDate = LocalDate.parse(followupDateStr); // Parse the date
                Date followupDate = (Date) Date.from(followupLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()); // Convert to Date
                lead.setFollowupDate(followupDate); // Set the followupDate
            }

            // Save the updated lead
            rrepo.save(lead);

            // Log success
            System.out.println("Lead saved successfully: " + lead);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            // Log the error
            System.out.println("Error updating lead stage: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false));
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
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

    @GetMapping("/manageleads")
    public String showLead(HttpSession session, HttpServletResponse response, Model model) {
        try {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

            if (session.getAttribute("adminid") != null) {
                List<Registration> leadlist = rrepo.findAll();
                model.addAttribute("leadlist", leadlist);
                return "/leadmanager/lmfG"; // Adjust according to your view structure
            } else {
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            return "redirect:/adminlogin";
        }
    }
    @GetMapping("/offlineuploadlogs") 
    public String showoimportleads() {
        return "/leadmanager/ilD2";
    }
    @GetMapping("/calendarpro")
    public String showcalendarpro() {
        return "calendarpro";
    }
    
    
    @GetMapping("/campaignmanager")
    public String showCampaignManager() {
        return "campaignmanager/cm";
        
    }
    @GetMapping("/formdesk") 
    public String showformdesk() {
        return "admin/adminDashboard";
    }
    
    @GetMapping("/marketing") 
    public String showmarketing() {
        return "admin/adminDashboard";
    }
    @GetMapping("/gu2")
    public String showoffgu2() {
        return "campaignmanager/gu2";
    }
    @GetMapping("/UploadCSV")
    public String showUploadCSV() {
        return "campaignmanager/UploadCSV";   
    }
    @GetMapping("/map")
    public String showmap() {
        return "campaignmanager/map";
    }
}