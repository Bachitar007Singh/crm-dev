package com.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "leads") // Or "students"
public class LeadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private LocalDate registrationDate;
    private String owner;
    private String duetime;
    private String status;
    private String action;
    
    // ... other fields ...

    // Constructors, getters, setters...
}