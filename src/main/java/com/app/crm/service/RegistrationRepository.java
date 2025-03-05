package com.app.crm.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.crm.model.Registration;

public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

}