package com.app.crm.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.crm.model.AdminLogin;

public interface AdminLoginRepository extends JpaRepository<AdminLogin, String> {

}
