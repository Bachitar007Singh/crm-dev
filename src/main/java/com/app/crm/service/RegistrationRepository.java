package com.app.crm.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.app.crm.model.Registration;

import java.time.LocalDate;
import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Integer> {
    @Query("SELECT r FROM Registration r WHERE r.counselorId = :counselorId")
    List<Registration> findByCounselorId(@Param("counselorId") Integer counselorId);
    
    @Query("SELECT r FROM Registration r WHERE r.leadStage = :leadStage")
    List<Registration> findByLeadStage(@Param("leadStage") String leadStage);

    @Query("SELECT r FROM Registration r WHERE r.counselorName = :counselorName")
    List<Registration> findByCounselorName(@Param("counselorName") String counselorName);

    @Query("SELECT r FROM Registration r WHERE r.campaignSource = :campaignSource")
    List<Registration> findByCampaignSource(@Param("campaignSource") String campaignSource);

	List<Registration> findByRegistrationDate(LocalDate localDate);
	
	
}