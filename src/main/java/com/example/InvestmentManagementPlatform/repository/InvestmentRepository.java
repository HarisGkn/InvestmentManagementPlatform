package com.example.InvestmentManagementPlatform.repository;

import com.example.InvestmentManagementPlatform.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByInvestmentType(String investmentType);

    List<Investment> findByAmountGreaterThan(BigDecimal amount);

    List<Investment> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT i FROM Investment i JOIN i.portfolio p JOIN p.user u WHERE u.username = :username AND u.active = true")
    List<Investment> findInvestmentsByUsername(@Param("username") String username);
}
