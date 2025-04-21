package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.model.Dividend;
import com.example.InvestmentManagementPlatform.model.Investment;
import com.example.InvestmentManagementPlatform.model.Portfolio;
import com.example.InvestmentManagementPlatform.model.User;
import com.example.InvestmentManagementPlatform.repository.DividendRepository;
import com.example.InvestmentManagementPlatform.repository.InvestmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DividendServiceTest {

    @InjectMocks
    private DividendService dividendService;

    @Mock
    private DividendRepository dividendRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    private User user;
    private Portfolio portfolio;
    private Investment investment;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("testuser");

        portfolio = new Portfolio();
        Field portfolioId = Portfolio.class.getDeclaredField("id");
        portfolioId.setAccessible(true);
        portfolioId.set(portfolio, 1L);
        portfolio.setUser(user);

        investment = new Investment();
        Field investmentId = Investment.class.getDeclaredField("id");
        investmentId.setAccessible(true);
        investmentId.set(investment, 100L);
        investment.setPortfolio(portfolio);
    }

    // Should return all dividends for an admin user
    @Test
    void testGetAllDividends_asAdmin() {
        Dividend d1 = new Dividend();
        when(dividendRepository.findAll()).thenReturn(List.of(d1));

        List<Dividend> result = dividendService.getAllDividends("admin", true);

        assertEquals(1, result.size());
    }

    // Should return only the user's dividends when user is not admin
    @Test
    void testGetAllDividends_asUser_filtered() {
        Dividend dividend = new Dividend();
        dividend.setInvestment(investment);

        when(dividendRepository.findAll()).thenReturn(List.of(dividend));

        List<Dividend> result = dividendService.getAllDividends("testuser", false);

        assertEquals(1, result.size());
    }

    // Should return an empty list if no dividends belong to the user
    @Test
    void testGetAllDividends_asUser_noMatch() {
        user.setUsername("notme");
        Dividend dividend = new Dividend();
        dividend.setInvestment(investment);

        when(dividendRepository.findAll()).thenReturn(List.of(dividend));

        List<Dividend> result = dividendService.getAllDividends("testuser", false);

        assertTrue(result.isEmpty());
    }

    // Should return dividends for a given investment ID
    @Test
    void testGetDividendsByInvestment() {
        Dividend d = new Dividend();
        when(dividendRepository.findByInvestmentId(100L)).thenReturn(List.of(d));

        List<Dividend> result = dividendService.getDividendsByInvestment(100L);

        assertEquals(1, result.size());
    }

    // Should create and return a new dividend when investment exists
    @Test
    void testCreateDividend_success() {
        when(investmentRepository.findById(100L)).thenReturn(Optional.of(investment));

        Dividend dividend = new Dividend(investment, LocalDate.now(), BigDecimal.valueOf(500));
        when(dividendRepository.save(any())).thenReturn(dividend);

        Dividend result = dividendService.createDividend(100L, LocalDate.now(), BigDecimal.valueOf(500));

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(500), result.getAmount());
        verify(dividendRepository).save(any());
    }

    // Should throw an exception if the investment is not found
    @Test
    void testCreateDividend_investmentNotFound() {
        when(investmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                dividendService.createDividend(999L, LocalDate.now(), BigDecimal.valueOf(100)));
    }
}
