
package com.app.crm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime; // Use LocalDateTime for combined date and time

@Entity
@Table(name = "leads")
public class Leads {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String subject;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String organizer;
    private String owner;
    private String dueTime; // You might want to use a more specific type if possible
    private String status;
    private String action; // Consider if this should be an Enum or related entity

    // Constructors (No-arg and All-arg)
    public Leads() {
    }

    public Leads(String name, String subject, LocalDateTime startDate, LocalDateTime endDate,
                 String organizer, String owner, String dueTime, String status, String action) {
        this.name = name;
        this.subject = subject;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organizer = organizer;
        this.owner = owner;
        this.dueTime = dueTime;
        this.status = status;
        this.action = action;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    // Optional: toString(), equals(), and hashCode()
    @Override
    public String toString() {
        return "Leads{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", organizer='" + organizer + '\'' +
                ", owner='" + owner + '\'' +
                ", dueTime='" + dueTime + '\'' +
                ", status='" + status + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}