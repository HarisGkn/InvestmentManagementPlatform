package com.example.InvestmentManagementPlatform.repository;

import com.example.InvestmentManagementPlatform.model.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<Dividend, Long> {

    List<Dividend> findByInvestmentId(Long investmentId);
}
