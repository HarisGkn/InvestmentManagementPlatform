package com.example.InvestmentManagementPlatform.repository;

import com.example.InvestmentManagementPlatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndActiveTrue(String username);
    Optional<User> findByIdAndActiveTrue(Long id);
}
