package com.example.InvestmentManagementPlatform.controller;

import com.example.InvestmentManagementPlatform.dto.AuthRequestDto;
import com.example.InvestmentManagementPlatform.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto authRequest) {
        if (authRequest.getUsername() == null || authRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Username is required"));
        }
        if (authRequest.getPassword() == null || authRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Password is required"));
        }
        try {
            System.out.println("Login request received for: " + authRequest.getUsername());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            ); // Attempt authentication with provided credentials
            System.out.println("Authentication successful for: " + authRequest.getUsername());
            String token = jwtUtil.generateToken(authRequest.getUsername()); // Generate JWT token for the authenticated user
            System.out.println("JWT Token generated successfully.");
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            // Prepare response body containing the JWT token
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            System.err.println("Invalid login attempt for: " + authRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Invalid username or password"));
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Something went wrong. Please try again."));
        }
    }
}
