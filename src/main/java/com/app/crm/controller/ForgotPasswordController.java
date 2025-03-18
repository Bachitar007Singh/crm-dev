package com.app.crm.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.crm.model.Counselor;
import com.app.crm.service.CounselorRepository;
import com.app.crm.service.OTPService;

@RestController
public class ForgotPasswordController {

    @Autowired
    private CounselorRepository crepo; // Use CounselorRepository

    @Autowired
    private OTPService otpService;

    private String currentEmail;
    private String currentOTP;
    private long otpGenerationTime; // Store OTP generation time

    @PostMapping("/forgot-password")
    public Map<String, Object> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email").toLowerCase().trim();
        Optional<Counselor> counselorOptional = crepo.findByEmail(email);
        Map<String, Object> response = new HashMap<>();

        if (counselorOptional.isEmpty()) {
            response.put("success", false);
            response.put("message", "Email not found!");
            return response;
        }

        // Generate and send OTP
        currentEmail = email;
        currentOTP = otpService.generateOTP();
        otpGenerationTime = System.currentTimeMillis(); // Store OTP generation time
        otpService.sendOTP(email, currentOTP);

        response.put("success", true);
        response.put("message", "OTP sent to your email!");
        return response;
    }

    @PostMapping("/verify-otp")
    public Map<String, Object> verifyOTP(@RequestBody Map<String, String> request) {
        String otp = request.get("otp");
        Map<String, Object> response = new HashMap<>();

        // Check if OTP is expired (1 minute = 60000 milliseconds)
        long currentTime = System.currentTimeMillis();
        if (currentTime - otpGenerationTime > 60000) {
            response.put("success", false);
            response.put("message", "OTP has expired. Please request a new OTP.");
            return response;
        }

        if (otp.equals(currentOTP)) {
            response.put("success", true);
            response.put("message", "OTP verified!");
        } else {
            response.put("success", false);
            response.put("message", "Invalid OTP!");
        }
        return response;
    }

    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        Optional<Counselor> counselorOptional = crepo.findByEmail(currentEmail); // Query Counselor table
        Map<String, Object> response = new HashMap<>();

        if (counselorOptional.isPresent()) {
            Counselor counselor = counselorOptional.get();
            counselor.setPassword(newPassword); // Update password
            crepo.save(counselor); // Save changes
            response.put("success", true);
            response.put("message", "Password reset successfully!");
        } else {
            response.put("success", false);
            response.put("message", "Failed to reset password!");
        }
        return response;
    }
}