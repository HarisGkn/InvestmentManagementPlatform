package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.model.Investment;
import com.example.InvestmentManagementPlatform.model.Portfolio;
import com.example.InvestmentManagementPlatform.model.TransactionType;
import com.example.InvestmentManagementPlatform.model.User;
import com.example.InvestmentManagementPlatform.repository.InvestmentRepository;
import com.example.InvestmentManagementPlatform.repository.PortfolioRepository;
import com.example.InvestmentManagementPlatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestmentServiceTest {

    @InjectMocks
    private InvestmentService investmentService;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private InvestmentAuditService investmentAuditService;

    private User user;
    private Portfolio portfolio;
    private Investment investment;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("testuser");
        user.setActive(true);

        portfolio = new Portfolio();
        setId(portfolio, "id", 10L);
        portfolio.setUser(user);

        investment = new Investment();
        investment.setInvestmentName("Stock A");
        investment.setAmount(BigDecimal.valueOf(1000));
        investment.setPurchaseDate(LocalDate.now());
        investment.setPortfolio(portfolio);
        investment.setActive(true);
        setId(investment, "id", 1L);
    }

    private void setId(Object target, String fieldName, Long value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // Should return all investments when requested by admin
    @Test
    void testGetAllInvestments_Admin() {
        when(investmentRepository.findAll()).thenReturn(List.of(investment));

        List<Investment> result = investmentService.getAllInvestments("admin", true);

        assertEquals(1, result.size());
    }

    // Should return only the user's investments when not admin
    @Test
    void testGetAllInvestments_User() {
        when(investmentRepository.findInvestmentsByUsername("testuser")).thenReturn(List.of(investment));

        List<Investment> result = investmentService.getAllInvestments("testuser", false);

        assertEquals(1, result.size());
    }

    // Should retrieve an existing investment by its ID
    @Test
    void testGetInvestmentById_Found() {
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        Optional<Investment> result = investmentService.getInvestmentById(1L);

        assertTrue(result.isPresent());
    }

    // Should save new investment and create a buy transaction
    @Test
    void testCreateInvestment_Success() {
        when(portfolioRepository.findById(10L)).thenReturn(Optional.of(portfolio));
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

        Investment result = investmentService.createInvestment(investment, "testuser");

        assertNotNull(result);
        verify(transactionService).createTransaction(
                result.getId(),
                result.getPurchaseDate(),
                TransactionType.BUY,
                result.getAmount(),
                "testuser"
        );
    }

    // Should throw exception when creating an investment without a valid portfolio
    @Test
    void testCreateInvestment_InvalidPortfolio() {
        Investment invalid = new Investment();
        assertThrows(RuntimeException.class, () -> investmentService.createInvestment(invalid, "user"));
    }

    // Should throw exception if the portfolio for the new investment does not exist
    @Test
    void testCreateInvestment_PortfolioNotFound() throws Exception {
        setId(investment.getPortfolio(), "id", 99L);
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> investmentService.createInvestment(investment, "testuser"));
    }

    // Should throw exception if a user creates an investment in another user's portfolio
    @Test
    void testCreateInvestment_Unauthorized() {
        user.setUsername("notYou");
        when(portfolioRepository.findById(10L)).thenReturn(Optional.of(portfolio));

        assertThrows(RuntimeException.class, () -> investmentService.createInvestment(investment, "testuser"));
    }

    // Should allow admin to update an investment and log the change
    @Test
    void testUpdateInvestment_Success_Admin() {
        Investment updated = new Investment();
        updated.setInvestmentName("New Stock");
        updated.setAmount(BigDecimal.valueOf(2000));
        updated.setPurchaseDate(LocalDate.now());
        updated.setSellDate(null);
        updated.setInvestmentType("Equity");
        updated.setCurrentValue(BigDecimal.valueOf(2200));
        updated.setProfitLoss(BigDecimal.valueOf(200));
        updated.setPortfolio(portfolio);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any())).thenReturn(investment);

        Investment result = investmentService.updateInvestment(1L, updated, "admin", true);

        assertNotNull(result);
        verify(investmentAuditService, atLeastOnce()).logInvestmentChange(any(), any(), any(), any());
    }

    // Should throw exception if a user tries to update another user's investment
    @Test
    void testUpdateInvestment_Unauthorized() {
        user.setUsername("originalOwner");
        investment.setPortfolio(portfolio);

        Investment updated = new Investment();
        updated.setPortfolio(portfolio);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        assertThrows(RuntimeException.class, () ->
                investmentService.updateInvestment(1L, updated, "otherUser", false));
    }

    // Should throw exception if the investment to update is not found
    @Test
    void testUpdateInvestment_NotFound() {
        when(investmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                investmentService.updateInvestment(999L, investment, "user", true));
    }

    // Should deactivate an investment when called by admin
    @Test
    void testDeactivateInvestment_Admin() {
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        investmentService.deactivateInvestment(1L, "admin", true);

        verify(investmentRepository).save(investment);
        assertFalse(investment.isActive());
    }

    // Should throw exception if a user tries to deactivate another user's investment
    @Test
    void testDeactivateInvestment_Unauthorized() {
        user.setUsername("someoneElse");
        investment.setPortfolio(portfolio);
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        assertThrows(RuntimeException.class, () ->
                investmentService.deactivateInvestment(1L, "wrongUser", false));
    }

    // Should throw exception if the investment to deactivate is not found
    @Test
    void testDeactivateInvestment_NotFound() {
        when(investmentRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                investmentService.deactivateInvestment(123L, "admin", true));
    }
}
