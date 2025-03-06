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

import java.time.LocalDate;
import java.util.ArrayList;

import com.app.crm.model.Leads;  // Import here as well!
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
		session.invalidate();
		return "redirect:/adminlogin";
	}

	@GetMapping("/admin/calendarpro") // Corrected mapping
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
			if (session.getAttribute("adminid") != null) {
				List<Registration> leadlist = rrepo.findAll();
				model.addAttribute("leadlist", leadlist);
				return "/admin/leadmanager"; // Adjust according to your view structure
			} else {
				return "redirect:/adminlogin";
			}
		} catch (Exception ex) {
			return "redirect:/adminlogin";
		}
	}

	@GetMapping("/events")
	public List<Event> getEvents() {
		// In a real application, you would fetch this from a database
		List<Event> events = new ArrayList<>();
		events.add(new Event("Event 1", "Meeting", "2024-03-10", "2024-03-10", "John Doe", "Jane Smith", "10:00 AM",
				"Completed"));
		events.add(new Event("Event 2", "Presentation", "2024-03-12", "2024-03-12", "Alice Johnson", "Bob Williams",
				"02:00 PM", "Pending"));
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
		public Event(String name, String subject, String startDate, String endDate, String organizer, String owner,
				String dueTime, String status) {
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

	private final LeadsRepository LeadsRepository;

    public AdminController(LeadsRepository LeadsRepository) {
        this.LeadsRepository = LeadsRepository;
    }

    @GetMapping("/admin/leads")
    public String showLeads(@RequestParam(value = "registrationDate", required = false) LocalDate registrationDate,
                            Model model) {
        List<Leads> leads;
        if (registrationDate != null) {
            leads = LeadsRepository.findByRegistrationDate(registrationDate);
        } else {
            leads = LeadsRepository.findAll();
        }
        model.addAttribute("leads", leads); // Consistent attribute name
        return "admin/events"; // Or whatever your template name is
    }
}