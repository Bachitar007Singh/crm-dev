package com.app.crm.controller;

import java.util.List;
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

import jakarta.persistence.EntityNotFoundException;
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
        // Save student registration
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
        rrepo.save(e);

        // Get all counselors and assign one randomly
        List<Counselor> counselors = crepo.findAll();
        Random random = new Random();
        Counselor assignedCounselor = counselors.get(random.nextInt(counselors.size()));

        // Debugging log - make sure counselor information is retrieved correctly
        System.out.println("Assigned Counselor: " + assignedCounselor.getName() + ", " + assignedCounselor.getPhoneNumber());

        // Add counselor info to the model
        attrib.addFlashAttribute("counselorName", assignedCounselor.getName());
        attrib.addFlashAttribute("counselorPhone", assignedCounselor.getPhoneNumber());


        // Add a flash attribute for redirect (if you need it on the next page)
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
    public String validateAdmin(@ModelAttribute AdminLoginDto dto, RedirectAttributes attrib, HttpSession session)
    {
    	try {
    		AdminLogin ad=adrepo.getById(dto.getUserid());
    		if(ad.getPassword().equals(dto.getPassword()))
    		{
    			//attrib.addFlashAttribute("msg", "Valid User");
    			session.setAttribute("adminid", dto.getUserid());
    			return "redirect:/admin/adminhome";
    		}
    		else {
    			attrib.addFlashAttribute("msg", "Invalid User");
    			return "redirect:/adminlogin";
    		}
    	}
    	catch(EntityNotFoundException ex){
    		attrib.addFlashAttribute("msg", "Admin not found");
    		return "redirect:/adminlogin";
    	}
    }

}