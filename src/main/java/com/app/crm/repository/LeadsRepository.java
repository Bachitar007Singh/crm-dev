package com.app.crm.repository;
import com.app.crm.model.Leads;  // Import here as well!
import java.util.List;
import java.time.LocalDate;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadsRepository extends JpaRepository<Leads, Long> {
	 List<Leads> findByRegistrationDate(LocalDate registrationDate);
    // You can add custom query methods here if needed
	
	
	//@Repository
	//public interface StudentRepository extends JpaRepository<Student, Long> {
	   

	    // You can add custom query methods here if needed
	}
