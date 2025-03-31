package com.app.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Date;
import java.time.LocalDate; // Import LocalDate

@Entity
@Table(name="registrations")
public class Registration {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private LocalDate registrationDate;

	    @Column(length = 50, nullable = false)
	    private String name;

	    @Column(length = 50, nullable = false)
	    private String email;

	    @Column(length = 50, nullable = false)
	    private String contactno;

	    @Column(length = 50, nullable = false)
	    private String otp;

	    @Column(length = 50, nullable = false)
	    private String state;

	    @Column(length = 50, nullable = false)
	    private String city;

	    @Column(length = 50, nullable = false)
	    private String institution;

	    @Column(length = 50, nullable = false)
	    private String course;

	    @Column(length = 50, nullable = false)
	    private String remark;

	    // ADDED counselorId field
	    @Column(name = "counselor_id") // Optional: Specify column name if needed
	    private Integer counselorId;

	    public Registration() {
	    }

	    // Getters and Setters

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public LocalDate getRegistrationDate() {
	        return registrationDate;
	    }

	    public void setRegistrationDate(LocalDate registrationDate) {
	        this.registrationDate = registrationDate;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getContactno() {
	        return contactno;
	    }

	    public void setContactno(String contactno) {
	        this.contactno = contactno;
	    }

	    public String getOtp() {
	        return otp;
	    }

	    public void setOtp(String otp) {
	        this.otp = otp;
	    }

	    public String getState() {
	        return state;
	    }

	    public void setState(String state) {
	        this.state = state;
	    }

	    public String getCity() {
	        return city;
	    }

	    public void setCity(String city) {
	        this.city = city;
	    }

	    public String getInstitution() {
	        return institution;
	    }

	    public void setInstitution(String institution) {
	        this.institution = institution;
	    }

	    public String getCourse() {
	        return course;
	    }

	    public void setCourse(String course) {
	        this.course = course;
	    }

	    public String getRemark() {
	        return remark;
	    }

	    public void setRemark(String remark) {
	        this.remark = remark;
	    }

	    // Getter and setter for counselorId
	    public Integer getCounselorId() {
	        return counselorId;
	    }

	    public void setCounselorId(Integer counselorId) {
	        this.counselorId = counselorId;
	    }

	    // Unused methods (remove or implement as needed)

	    public void setLeadStage(String leadStage) {
	        // TODO: Implement or remove
	    }

	    public void setCounselorName(String counselorName) {
	        // TODO: Implement or remove
	    }

	    public void setLeadRemark(String leadRemark) {
	        // TODO: Implement or remove
	    }

	    public void setFollowupDate(java.sql.Date followupDate) {
	        // TODO: Implement or remove
	    }
	}