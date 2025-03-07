package com.app.crm.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.crm.dto.AdminLoginDto;
import com.app.crm.dto.RegistrationDto;
import com.app.crm.model.AdminLogin;
import com.app.crm.model.Counselor;
import com.app.crm.model.Registration;
import com.app.crm.service.RegistrationRepository;
import com.app.crm.util.SessionRegistry;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.app.crm.service.AdminLoginRepository;
import com.app.crm.service.CounselorRepository;

@Controller
public class Maincontroller {

    @Autowired
    RegistrationRepository rrepo;

    @Autowired
    CounselorRepository crepo;
    @Autowired
    AdminLoginRepository adrepo;
    
    @GetMapping("/")
	public String showIndex()
	{
		return "index";
	}
    @GetMapping("/adminlogin")
	public String showAdminLogin(Model model)
	{
    	AdminLoginDto dto=new AdminLoginDto();
    	model.addAttribute("dto",dto);
		return "adminlogin";
	}

    @GetMapping("/home")
    public String showHome(Model model) {
        RegistrationDto dto = new RegistrationDto();
        model.addAttribute("dto", dto);
        return "home";
    }

    @PostMapping("/home")
    public String save(@ModelAttribute RegistrationDto dto, RedirectAttributes attrib, Model model) {
        Registration e = new Registration();
        e.setName(dto.getName());
        e.setEmail(dto.getEmail());
        e.setContactno(dto.getContactno());
        e.setOtp(dto.getOtp());
        e.setState(dto.getState());
        e.setCity(dto.getCity());
        e.setInstitution(dto.getInstitution());
        e.setCourse(dto.getCourse());
        e.setRemark(dto.getRemark());
        e.setRegistrationDate(new Date()); // Set the registration date to the current date

        // Assign a counselor and save the registration
        List<Counselor> counselors = crepo.findByActiveTrue();
        Random random = new Random();
        Counselor assignedCounselor = counselors.get(random.nextInt(counselors.size()));
        e.setCounselorId(assignedCounselor.getId());
        e.setCounselorName(assignedCounselor.getName());

        rrepo.save(e);

        attrib.addFlashAttribute("counselorName", assignedCounselor.getName());
        attrib.addFlashAttribute("counselorPhone", assignedCounselor.getPhoneNumber());
        attrib.addFlashAttribute("msg", "Registration is Done. Your assigned counselor is "
                + assignedCounselor.getName() + " (Phone: "
                + assignedCounselor.getPhoneNumber() + ")");
        return "redirect:/counselor-info";
    }


    @GetMapping("/counselor-info")
    public String showCounselorInfo(Model model) {
       
        return "counselor-info";
    }
    
    @PostMapping("/adminlogin")
    public String validateAdmin(@ModelAttribute AdminLoginDto dto, RedirectAttributes attrib, HttpSession session, HttpServletRequest request) {
        try {
            // Check if the user is already logged in
            String activeSessionId = SessionRegistry.getSessionId(dto.getUserid());
            if (activeSessionId != null) {
                // Invalidate the previous session
                HttpSession oldSession = request.getSession(false); // Get the old session without creating a new one
                if (oldSession != null) {
                    oldSession.invalidate(); // Invalidate the old session
                }
                session = request.getSession(true); // Create a new session
            }

            // Check if the user is an admin
            AdminLogin admin = adrepo.findById(dto.getUserid()).orElse(null);
            if (admin != null && admin.getPassword().equals(dto.getPassword())) {
                session.setAttribute("adminid", dto.getUserid()); // Set attributes on the new session
                session.setAttribute("role", "Admin");
                session.setAttribute("name", "Admin");
                SessionRegistry.addSession(dto.getUserid(), session.getId()); // Register the new session
                return "redirect:/dashboard";
            }

            // Check if the user is a counselor
            Optional<Counselor> counselorOptional = crepo.findByEmail(dto.getUserid());
            Counselor counselor = counselorOptional.orElse(null); // Handle Optional<Counselor>
            if (counselor != null && counselor.getPassword().equals(dto.getPassword())) {
                session.setAttribute("counselorId", counselor.getId());
                session.setAttribute("role", counselor.getRole());
                session.setAttribute("name", counselor.getName());
                SessionRegistry.addSession(dto.getUserid(), session.getId()); // Register the new session
                return "redirect:/dashboard";
            }

            // If no match, show error
            attrib.addFlashAttribute("msg", "Invalid Email or Password");
            return "redirect:/adminlogin";

        } catch (EntityNotFoundException ex) {
            attrib.addFlashAttribute("msg", "User not found");
            return "redirect:/adminlogin";
        }
    }
}