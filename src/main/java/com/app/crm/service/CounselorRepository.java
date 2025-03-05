package com.app.crm.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.app.crm.model.Counselor;

import jakarta.transaction.Transactional;

import java.util.List;

public interface CounselorRepository extends JpaRepository<Counselor, Integer> {
    List<Counselor> findByActiveTrue(); // Fetch only active counselors

    @Modifying
    @Transactional
    @Query("UPDATE Counselor c SET c.active = false WHERE c.id = :id")
    void deactivateCounselor(@Param("id") int id); // Deactivate counselor
}