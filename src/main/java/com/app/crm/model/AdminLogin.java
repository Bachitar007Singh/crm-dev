package com.app.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="adminlogin")
public class AdminLogin {

	@Id
	@Column(length=50)
	private String userid;
	@Column(length=30, nullable = false)
	private String password;
	private String Role;

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		// TODO Auto-generated method stub
		return null;
	
//	public Object getId() {
		// TODO Auto-generated method stub
		//return null;
	}
	
}