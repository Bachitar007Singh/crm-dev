package com.app.crm.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.app.crm.dto.AdminLoginDto;
import com.app.crm.dto.CounselorDto;
import com.app.crm.model.AdminLogin;
import com.app.crm.model.Counselor;
import com.app.crm.model.Registration;
import com.app.crm.service.RegistrationRepository;
import com.app.crm.util.SessionRegistry;
import com.app.crm.service.AdminLoginRepository;
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
    @Autowired
    AdminLoginRepository adminLoginRepository; // Ensure this repository is autowired
    

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
    @PostMapping("/login")
    public String login(@ModelAttribute AdminLoginDto dto, HttpSession session) {
        AdminLogin admin = adminLoginRepository.findByUserid(dto.getUserid());
        if (admin != null && admin.getPassword().equals(dto.getPassword())) {
            session.setAttribute("adminid", admin.getUserid());
            session.setAttribute("role", admin.getRole());
            // If the user is a counselor, set counselorId
            if ("Employee".equals(admin.getRole())) {
                session.setAttribute("counselorId", admin.getId()); // Assuming counselorId is stored in the admin entity
            }
            return "redirect:/admin/adminhome";
        } else {
            return "redirect:/adminlogin";
        }
    }
    @GetMapping("/calendarpro")
    public String showcalendarpro(HttpSession session, HttpServletResponse response, Model model) {
        try {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

            // Debug logs
            System.out.println("Admin ID: " + session.getAttribute("adminid"));
            System.out.println("Counselor ID: " + session.getAttribute("counselorId"));
            System.out.println("Role: " + session.getAttribute("role"));

            if (session.getAttribute("adminid") != null || session.getAttribute("counselorId") != null) {
                String role = (String) session.getAttribute("role");
                if (role == null || (!role.equals("Admin") && !role.equals("Manager") && !role.equals("Employee"))) {
                    System.out.println("Invalid role: Redirecting to login page");
                    return "redirect:/adminlogin";
                }

                List<Registration> leadlist;
                if ("Employee".equals(role)) {
                    Integer counselorId = (Integer) session.getAttribute("counselorId");
                    leadlist = rrepo.findByCounselorId(counselorId);
                } else {
                    leadlist = rrepo.findAll();
                }

                // Filter out null registration dates
                leadlist = leadlist.stream()
                    .filter(registration -> registration.getRegistrationDate() != null)
                    .collect(Collectors.toList());

                // Sort the leadlist by registrationDate in descending order
                leadlist.sort((r1, r2) -> {
                    Date date1 = r1.getRegistrationDate();
                    Date date2 = r2.getRegistrationDate();
                    if (date1 == null && date2 == null) return 0;
                    if (date1 == null) return -1; // Treat null as earlier
                    if (date2 == null) return 1;  // Treat null as earlier
                    return date2.compareTo(date1); // Sort in descending order
                });

                // Fetch all active counselors
                List<Counselor> counselors = crepo.findByActiveTrue();
                model.addAttribute("counselors", counselors);
                model.addAttribute("leadlist", leadlist);
                return "/admin/calendarpro";
            } else {
                System.out.println("Session validation failed: Redirecting to login page");
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "redirect:/adminlogin";
        }
    }
    // Helper method to format the due date and calculate time remaining
    private String formatDueDate(Date followupDate, String followupTime) {
        if (followupDate == null || followupTime == null) {
            return "N/A";
        }

        // Parse followup date and time
        LocalDateTime followupDateTime = LocalDateTime.of(
            followupDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            LocalTime.parse(followupTime, DateTimeFormatter.ofPattern("hh:mm a"))
        );

        // Get current date and time
        LocalDateTime now = LocalDateTime.now();

        // Calculate time difference
        Duration duration = Duration.between(now, followupDateTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        // Format the result
        return followupDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")) +
               " (" + (hours > 0 ? hours + "hr " : "") + minutes + "min remaining)";
    }
    
    @GetMapping("/lead-details")
    public String showLeadDetails(@RequestParam("id") int leadId, Model model) {
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
    // Import Date

    @PostMapping("/update-lead-stage")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> updateLeadStage(@RequestBody Map<String, String> data) {
        try {
            // Log the received data
            System.out.println("Update lead stage endpoint called with data: " + data);

            // Parse the data
            int leadId = Integer.parseInt(data.get("leadId"));
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
                Date followupDate = Date.from(followupLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()); // Convert to Date
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