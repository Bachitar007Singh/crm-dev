package com.app.crm.model;



import java.time.LocalDateTime;

public class Leads {
    private String name;
    private String subject;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String organizer;
    private String owner;
    private String dueTime;
    private String status;

    // Constructors (optional)
    public Leads() {
    }

    public Leads(String name, String subject, LocalDateTime startDate, LocalDateTime endDate, String organizer, String owner, String dueTime, String status) {
        this.name = name;
        this.subject = subject;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organizer = organizer;
        this.owner = owner;
        this.dueTime = dueTime;
        this.status = status;
    }

    // Getters and Setters (Required)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}