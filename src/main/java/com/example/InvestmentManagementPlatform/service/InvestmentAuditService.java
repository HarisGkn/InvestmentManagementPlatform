package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.model.Investment;
import com.example.InvestmentManagementPlatform.model.InvestmentAudit;
import com.example.InvestmentManagementPlatform.repository.InvestmentAuditRepository;
import com.example.InvestmentManagementPlatform.repository.InvestmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestmentAuditService {

    private final InvestmentAuditRepository auditRepository;
    private final InvestmentRepository investmentRepository;

    @Autowired
    public InvestmentAuditService(InvestmentAuditRepository auditRepository,
                                  InvestmentRepository investmentRepository) {
        this.auditRepository = auditRepository;
        this.investmentRepository = investmentRepository;
    }

    @Transactional
    public InvestmentAudit logInvestmentChange(Long investmentId, String changeType,
                                               BigDecimal oldValue, BigDecimal newValue) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new RuntimeException("Investment not found with id " + investmentId));

        InvestmentAudit audit = new InvestmentAudit(
                investment,
                changeType,
                LocalDateTime.now(),
                oldValue,
                newValue
        );
        return auditRepository.save(audit);
    }

    @Transactional(readOnly = true)
    public List<InvestmentAudit> getAllAudits() {
        return auditRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<InvestmentAudit> getAuditsForUser(String username) {
        return auditRepository.findAll().stream()
                .filter(audit -> audit.getInvestment()
                        .getPortfolio()
                        .getUser()
                        .getUsername()
                        .equals(username))
                .collect(Collectors.toList());
    }
}
