package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.model.Role;
import com.example.InvestmentManagementPlatform.model.User;
import com.example.InvestmentManagementPlatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    // Should save a new user and return the saved user
    @Test
    void createUser_shouldSaveUser() {
        User user = new User("alice", "pass123", Role.USER);
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.createUser(user);

        assertEquals("alice", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    // Should find and return a user by username if present
    @Test
    void findByUsername_shouldReturnUser() {
        User user = new User("bob", "pwd", Role.USER);
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("bob");

        assertTrue(result.isPresent());
        assertEquals("bob", result.get().getUsername());
    }

    // Should retrieve a user by their ID
    @Test
    void getUserById_shouldReturnUser() {
        User user = new User("jane", "pwd", Role.USER);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(10L);

        assertTrue(result.isPresent());
        assertEquals("jane", result.get().getUsername());
    }

    // Should mark an existing user as inactive and save the change
    @Test
    void deactivateUser_shouldSetUserInactive() {
        User user = new User("chris", "pwd", Role.USER);
        user.setActive(true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.deactivateUser(2L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    // Should throw exception when trying to deactivate a non-existing user
    @Test
    void deactivateUser_shouldThrowIfUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deactivateUser(99L));
    }
}
