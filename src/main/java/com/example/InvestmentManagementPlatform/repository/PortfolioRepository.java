package com.example.InvestmentManagementPlatform.repository;

import com.example.InvestmentManagementPlatform.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByOwner(String owner);

    List<Portfolio> findByTotalValueGreaterThan(BigDecimal value);

    List<Portfolio> findByPortfolioNameContaining(String keyword);

    List<Portfolio> findByUser_Username(String username);

    List<Portfolio> findByUser_UsernameAndActiveTrue(String username);

    List<Portfolio> findByActiveTrue();
}
