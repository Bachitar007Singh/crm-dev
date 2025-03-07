package com.app.crm.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name="registrations")
public class Registration {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(length=50, nullable=false)
	private String name;
	
	@Column(length=50, nullable=false)
	private String email;
	
	@Column(length=50, nullable=false)
	private String contactno;
	
	@Column(length=50, nullable=false)
	private String otp;
	
	@Column(length=50, nullable=false)
	private String state;
	
	@Column(length=50, nullable=false)
	private String city;
	
	@Column(length=50, nullable=false)
	private String institution;
	
	@Column(length=50, nullable=false)
	private String course;
	
	@Column(length=50, nullable=false)
	private String remark;
	
	@Column(length = 50)
    private String leadStage; // Hot, Cold, Untouched, Warm, Closed, Not Reachable

    @Column(length = 50)
    private String counselorName; // Name of the assigned counselor

    @Column(length = 50)
    private String campaignSource; // Walkin, Email, Ad, Referral

    // Getters and Setters for new fields
    public String getLeadStage() {
        return leadStage;
    }

    public void setLeadStage(String leadStage) {
        this.leadStage = leadStage;
    }

    public String getCounselorName() {
        return counselorName;
    }

    public void setCounselorName(String counselorName) {
        this.counselorName = counselorName;
    }

    public String getCampaignSource() {
        return campaignSource;
    }

    public void setCampaignSource(String campaignSource) {
        this.campaignSource = campaignSource;
    }
	
	@Column(name = "counselor_id")
    private Integer counselorId; // Add this field

	@Column(name = "registration_date")
    @Temporal(TemporalType.DATE)
    private Date registrationDate; // Add this field

    // Getters and Setters
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
	
    // Getters and Setters
    public Integer getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(Integer counselorId) {
        this.counselorId = counselorId;
    }

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
}
