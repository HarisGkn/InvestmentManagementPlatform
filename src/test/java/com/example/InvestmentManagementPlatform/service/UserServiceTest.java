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

    @Test
    void createUser_shouldSaveUser() {
        User user = new User("alice", "pass123", Role.USER);
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.createUser(user);

        assertEquals("alice", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findByUsername_shouldReturnUser() {
        User user = new User("bob", "pwd", Role.USER);
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("bob");

        assertTrue(result.isPresent());
        assertEquals("bob", result.get().getUsername());
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User("jane", "pwd", Role.USER);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(10L);

        assertTrue(result.isPresent());
        assertEquals("jane", result.get().getUsername());
    }

    @Test
    void deactivateUser_shouldSetUserInactive() {
        User user = new User("chris", "pwd", Role.USER);
        user.setActive(true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.deactivateUser(2L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_shouldThrowIfUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deactivateUser(99L));
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        User user = new User("john", "secret", Role.ADMIN);
        user.setActive(true);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("john");

        assertEquals("john", userDetails.getUsername());
        assertEquals("secret", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_shouldThrowIfUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("ghost"));
    }

    @Test
    void loadUserByUsername_shouldThrowIfUserInactive() {
        User user = new User("inactive", "nopass", Role.USER);
        user.setActive(false);
        when(userRepository.findByUsername("inactive")).thenReturn(Optional.of(user));

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("inactive"));
    }
}
