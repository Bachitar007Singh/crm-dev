
package com.app.crm.repository;

import com.app.crm.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Count male students
    @Query("SELECT COUNT(s) FROM Student s WHERE s.gender = 'Male'")
    long countMaleStudents();

    // Count female students
    @Query("SELECT COUNT(s) FROM Student s WHERE s.gender = 'Female'")
    long countFemaleStudents();

    // Get all students' ages
    @Query("SELECT YEAR(CURRENT_DATE) - YEAR(s.dateOfBirth) FROM Student s")
    List<Integer> getAllAges();
}