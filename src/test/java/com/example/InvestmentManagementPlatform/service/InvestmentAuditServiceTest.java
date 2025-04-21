package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.model.Investment;
import com.example.InvestmentManagementPlatform.model.InvestmentAudit;
import com.example.InvestmentManagementPlatform.model.Portfolio;
import com.example.InvestmentManagementPlatform.model.User;
import com.example.InvestmentManagementPlatform.repository.InvestmentAuditRepository;
import com.example.InvestmentManagementPlatform.repository.InvestmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestmentAuditServiceTest {

    @InjectMocks
    private InvestmentAuditService investmentAuditService;

    @Mock
    private InvestmentAuditRepository auditRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    private Investment investment;
    private Portfolio portfolio;
    private User user;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("testuser");

        portfolio = new Portfolio();
        Field portfolioIdField = Portfolio.class.getDeclaredField("id");
        portfolioIdField.setAccessible(true);
        portfolioIdField.set(portfolio, 1L);
        portfolio.setUser(user);

        investment = new Investment();
        Field idField = Investment.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(investment, 10L);
        investment.setPortfolio(portfolio);
    }

    // Should log and return an audit entry for a valid investment
    @Test
    void testLogInvestmentChange_Success() {
        when(investmentRepository.findById(10L)).thenReturn(Optional.of(investment));

        InvestmentAudit audit = new InvestmentAudit(
                investment, "Value Update", LocalDateTime.now(),
                BigDecimal.valueOf(100), BigDecimal.valueOf(120)
        );

        when(auditRepository.save(any())).thenReturn(audit);

        InvestmentAudit result = investmentAuditService.logInvestmentChange(
                10L, "Value Update", BigDecimal.valueOf(100), BigDecimal.valueOf(120));

        assertNotNull(result);
        assertEquals("Value Update", result.getChangeType());
        verify(auditRepository).save(any());
    }

    // Should throw exception when logging change for non-existent investment
    @Test
    void testLogInvestmentChange_InvestmentNotFound() {
        when(investmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                investmentAuditService.logInvestmentChange(999L, "Type", BigDecimal.ONE, BigDecimal.TEN));
    }

    // Should return all investment audit records
    @Test
    void testGetAllAudits() {
        InvestmentAudit audit = new InvestmentAudit(
                investment, "Change", LocalDateTime.now(),
                BigDecimal.ONE, BigDecimal.TEN
        );
        when(auditRepository.findAll()).thenReturn(List.of(audit));

        List<InvestmentAudit> result = investmentAuditService.getAllAudits();

        assertEquals(1, result.size());
    }

    // Should return audit entries for the specified user
    @Test
    void testGetAuditsForUser() {
        InvestmentAudit audit = new InvestmentAudit(
                investment, "Change", LocalDateTime.now(),
                BigDecimal.ONE, BigDecimal.TEN
        );
        when(auditRepository.findAll()).thenReturn(List.of(audit));

        List<InvestmentAudit> result = investmentAuditService.getAuditsForUser("testuser");

        assertEquals(1, result.size());
    }

    // Should return an empty list if the user has no audit entries
    @Test
    void testGetAuditsForUser_NoMatch() {
        InvestmentAudit audit = new InvestmentAudit(
                investment, "Change", LocalDateTime.now(),
                BigDecimal.ONE, BigDecimal.TEN
        );
        when(auditRepository.findAll()).thenReturn(List.of(audit));

        List<InvestmentAudit> result = investmentAuditService.getAuditsForUser("someone_else");

        assertTrue(result.isEmpty());
    }
}
