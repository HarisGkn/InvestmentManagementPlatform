package com.example.InvestmentManagementPlatform.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordChecker {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "password123";
        String hashedPassword = encoder.encode(rawPassword);

        System.out.println("🆕 New Hashed Password: " + hashedPassword);
    }
}
