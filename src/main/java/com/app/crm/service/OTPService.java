package com.app.crm.service;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private JavaMailSender mailSender;

    // Generate a random 6-digit OTP
    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(otp);
    }

    // Send OTP to the user's email
    public void sendOTP(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); // Recipient email
        message.setSubject("Your OTP for Password Reset"); // Email subject
        message.setText("Your OTP is: " + otp); // Email body
        mailSender.send(message); // Send the email
    }
}