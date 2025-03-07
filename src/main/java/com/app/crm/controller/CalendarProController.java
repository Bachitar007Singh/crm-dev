package com.app.crm.controller;

import com.app.crm.model.Leads; // Assuming you have a Leads model
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Use Spring's Model
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CalendarProController {
	@GetMapping("/test")
	public String testPage() {
	    return "test"; // Look for test.html in templates/
	}
	@GetMapping("/admin/calendarpro")
	public String showCalendarPro(Model model) {
	    // Dummy data for demonstration
	    List<Leads> leads = new ArrayList<>();
	    Leads lead = new Leads();
	    lead.setName("Sido Tubu");
	    lead.setSubject("Follow Up Sido Tubu");
	    lead.setStartDate(LocalDateTime.of(2025, 3, 6, 10, 56));
	    lead.setEndDate(LocalDateTime.of(2025, 3, 6, 11, 26));
	    lead.setOrganizer("Geeta Bhati");
	    lead.setOwner("Geeta Bhati");
	    lead.setDueTime("3 min");
	    lead.setStatus("Overdue");
	    leads.add(lead);

	    System.out.println("Leads: " + leads); // Debugging statement
	    model.addAttribute("leads", leads);
	    return "admin/calendarpro";
	}
}