package com.example.InvestmentManagementPlatform.controller;

import com.example.InvestmentManagementPlatform.dto.UserDto;
import com.example.InvestmentManagementPlatform.dto.UserRegistrationDto;
import com.example.InvestmentManagementPlatform.model.User;
import com.example.InvestmentManagementPlatform.service.UserService;
import com.example.InvestmentManagementPlatform.util.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user with encoded password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {

        String encodedPassword = passwordEncoder.encode(userRegistrationDto.getPassword());

        User newUser = new User(userRegistrationDto.getUsername(), encodedPassword, userRegistrationDto.getRole());

        User savedUser = userService.createUser(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(Mapper.toUserDto(savedUser));
    }
}
