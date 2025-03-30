package com.example.InvestmentManagementPlatform.repository;

import com.example.InvestmentManagementPlatform.model.InvestmentAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentAuditRepository extends JpaRepository<InvestmentAudit, Long> {

    List<InvestmentAudit> findByInvestmentId(Long investmentId);
}
