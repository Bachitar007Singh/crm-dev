package com.app.crm.service;

import com.app.crm.model.Leads;  // Import if you use Leads here
import com.app.crm.repository.LeadsRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class LeadsService {

    private final LeadsRepository LeadsRepository;

    public LeadsService(LeadsRepository LeadsRepository) {
        this.LeadsRepository = LeadsRepository;
    }

    public List<Leads> getLeadsByDate(LocalDate date) {
        if (date != null) {
            return LeadsRepository.findByRegistrationDate(date);
        } else {
            return LeadsRepository.findAll();
        }
    }
}