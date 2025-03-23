package com.app.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.yaml.snakeyaml.events.Event;

import com.app.crm.dto.CounselorDto;
import com.app.crm.model.Counselor;
import com.app.crm.model.Registration;
import com.app.crm.service.RegistrationRepository;
import com.app.crm.service.CounselorRepository; // Import CounselorRepository

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.util.ArrayList;


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
                long totalLeads = rrepo.count();
                model.addAttribute("totalLeads", totalLeads);
                return "admin/adminhome"; // Ensure this matches the template path
            } else {
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            return "redirect:/adminlogin";
        }
    }
<<<<<<< HEAD

=======
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
                    session.setAttribute("counselorId", admin.getId());
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

    @GetMapping("/calendarpro")
    public String showcalendarpro(HttpSession session, HttpServletResponse response, Model model) {
        try {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

            // Debug logs
            System.out.println("Session - adminid: " + session.getAttribute("adminid"));
            System.out.println("Session - counselorId: " + session.getAttribute("counselorId"));
            System.out.println("Session - role: " + session.getAttribute("role"));

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

                // Filter and sort leads
                leadlist = leadlist.stream()
                    .filter(registration -> registration.getRegistrationDate() != null)
                    .collect(Collectors.toList());

                leadlist.sort((r1, r2) -> {
                    Date date1 = r1.getRegistrationDate();
                    Date date2 = r2.getRegistrationDate();
                    if (date1 == null && date2 == null) return 0;
                    if (date1 == null) return -1;
                    if (date2 == null) return 1;
                    return date2.compareTo(date1);
                });

                // Fetch active counselors
                List<Counselor> counselors = crepo.findByActiveTrue();
                model.addAttribute("counselors", counselors);
                model.addAttribute("leadlist", leadlist);
                return "admin/calendarpro"; // Ensure this matches the template path
            } else {
                System.out.println("Session validation failed: Redirecting to login page");
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "redirect:/adminlogin";
        }
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
    
>>>>>>> 38f604b (this is testing version with previous issues avoid it but if you want to solve error go for it)
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/adminlogin";
    }
    
    @GetMapping("/admin/calendarpro")  // Corrected mapping
    public String calendarpro() {
        return "calendarpro"; // Assuming "calendarpro.html" is in your templates folder
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
<<<<<<< HEAD
            if (session.getAttribute("adminid") != null) {
                List<Registration> leadlist = rrepo.findAll();
                model.addAttribute("leadlist", leadlist);
                return "/admin/leadmanager"; // Adjust according to your view structure
=======

            // Debug logs
            System.out.println("Session - adminid: " + session.getAttribute("adminid"));
            System.out.println("Session - counselorId: " + session.getAttribute("counselorId"));
            System.out.println("Session - role: " + session.getAttribute("role"));

            if (session.getAttribute("adminid") != null || session.getAttribute("counselorId") != null) {
                String role = (String) session.getAttribute("role");
                List<Registration> leadlist;

                if ("Employee".equals(role)) {
                    Integer counselorId = (Integer) session.getAttribute("counselorId");
                    leadlist = rrepo.findByCounselorId(counselorId);
                } else {
                    leadlist = rrepo.findAll();
                }

                // Sort leads
                leadlist.sort((r1, r2) -> r2.getRegistrationDate().compareTo(r1.getRegistrationDate()));

                // Fetch active counselors
                List<Counselor> counselors = crepo.findByActiveTrue();
                model.addAttribute("counselors", counselors);
                model.addAttribute("leadlist", leadlist);
                return "admin/leadmanager"; // Ensure this matches the template path
>>>>>>> 38f604b (this is testing version with previous issues avoid it but if you want to solve error go for it)
            } else {
                return "redirect:/adminlogin";
            }
        } catch (Exception ex) {
            return "redirect:/adminlogin";
        }
    }
<<<<<<< HEAD
   
 
   

  

        @GetMapping("/events")
        public List<Event> getEvents() {
            // In a real application, you would fetch this from a database
            List<Event> events = new ArrayList<>();
            events.add(new Event("Event 1", "Meeting", "2024-03-10", "2024-03-10", "John Doe", "Jane Smith", "10:00 AM", "Completed"));
            events.add(new Event("Event 2", "Presentation", "2024-03-12", "2024-03-12", "Alice Johnson", "Bob Williams", "02:00 PM", "Pending"));
            return events;
        }

        class Event {
            private String name;
            private String subject;
            private String startDate;
            private String endDate;
            private String organizer;
            private String owner;
            private String dueTime;
            private String status;
            // Constructor, getters, and setters
            public Event(String name, String subject, String startDate, String endDate, String organizer, String owner, String dueTime, String status) {
                this.name = name;
                this.subject = subject;
                this.startDate = startDate;
                this.endDate = endDate;
                this.organizer = organizer;
                this.owner = owner;
                this.dueTime = dueTime;
                this.status = status;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getSubject() {
                return subject;
            }

            public void setSubject(String subject) {
                this.subject = subject;
            }

            public String getStartDate() {
                return startDate;
            }

            public void setStartDate(String startDate) {
                this.startDate = startDate;
            }

            public String getEndDate() {
                return endDate;
            }

            public void setEndDate(String endDate) {
                this.endDate = endDate;
            }

            public String getOrganizer() {
                return organizer;
            }

            public void setOrganizer(String organizer) {
                this.organizer = organizer;
            }

            public String getOwner() {
                return owner;
            }

            public void setOwner(String owner) {
                this.owner = owner;
            }

            public String getDueTime() {
                return dueTime;
            }

            public void setDueTime(String dueTime) {
                this.dueTime = dueTime;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
=======


>>>>>>> 38f604b (this is testing version with previous issues avoid it but if you want to solve error go for it)

}