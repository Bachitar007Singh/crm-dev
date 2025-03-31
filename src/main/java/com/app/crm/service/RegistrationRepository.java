package com.app.crm.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.crm.model.Registration;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

	List<Registration> findByCounselorId(Integer counselorId);

}