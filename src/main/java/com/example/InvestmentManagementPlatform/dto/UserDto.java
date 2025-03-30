package com.example.InvestmentManagementPlatform.dto;

import com.example.InvestmentManagementPlatform.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 50, message = "Username must be less than 50 characters")
    private String username;

    private Role role;
    private boolean active;

    public UserDto() {}

    public UserDto(String username, Role role, boolean active) {
        this.username = username;
        this.role = role;
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
