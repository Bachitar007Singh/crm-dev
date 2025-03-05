package com.app.crm.service;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.crm.model.Counselor;

public interface CounselorRepository extends JpaRepository<Counselor, Integer> {
}
