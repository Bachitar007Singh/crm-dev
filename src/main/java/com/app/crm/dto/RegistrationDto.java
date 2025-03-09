package com.app.crm.dto;

public class RegistrationDto {

	private String name;
	private String email;
	private String contactno;
	private String otp;
	private String state;
	private String city;
	private String institution;
	private String course;
	private String remark;
	 private String leadStage;
	    private String counselorName;
	    private String campaignSource;
	    private String followupstatus;
		
		
		public String getFollowupstatus() {
			return followupstatus;
		}

		public void setFollowupstatus(String followupstatus) {
			this.followupstatus = followupstatus;
		}
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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