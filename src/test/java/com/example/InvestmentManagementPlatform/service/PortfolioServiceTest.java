package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.dto.PortfolioDto;
import com.example.InvestmentManagementPlatform.model.Portfolio;
import com.example.InvestmentManagementPlatform.model.User;
import com.example.InvestmentManagementPlatform.repository.PortfolioRepository;
import com.example.InvestmentManagementPlatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioServiceTest {

    @InjectMocks
    private PortfolioService portfolioService;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPortfolios_asAdmin() {
        Portfolio portfolio = new Portfolio("My Portfolio", "owner1", BigDecimal.TEN, new User());
        portfolio.setActive(true);
        when(portfolioRepository.findByActiveTrue()).thenReturn(List.of(portfolio));

        List<PortfolioDto> result = portfolioService.getAllPortfolios("admin", true);

        assertEquals(1, result.size());
        verify(portfolioRepository).findByActiveTrue();
    }

    @Test
    void testGetAllPortfolios_asUser() {
        Portfolio portfolio = new Portfolio("My Portfolio", "owner1", BigDecimal.TEN, new User());
        portfolio.setActive(true);
        when(portfolioRepository.findByUser_UsernameAndActiveTrue("user1")).thenReturn(List.of(portfolio));

        List<PortfolioDto> result = portfolioService.getAllPortfolios("user1", false);

        assertEquals(1, result.size());
        verify(portfolioRepository).findByUser_UsernameAndActiveTrue("user1");
    }

    @Test
    void testGetPortfolioById_asAdmin() {
        Portfolio portfolio = new Portfolio("Test", "owner", BigDecimal.ONE, new User());
        portfolio.setActive(true);
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        Optional<PortfolioDto> result = portfolioService.getPortfolioById(1L, "admin", true);

        assertTrue(result.isPresent());
    }

    @Test
    void testGetPortfolioById_asUser_unauthorized() {
        User user = new User();
        user.setUsername("owner");
        Portfolio portfolio = new Portfolio("Test", "owner", BigDecimal.ONE, user);
        portfolio.setActive(true);
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        Optional<PortfolioDto> result = portfolioService.getPortfolioById(1L, "wrongUser", false);

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreatePortfolio_success() {
        User user = new User();
        user.setUsername("charis");
        user.setActive(true);

        PortfolioDto dto = new PortfolioDto(null, "Test Portfolio", "charis",
                BigDecimal.valueOf(100), List.of(), null, true);

        when(userRepository.findByUsername("charis")).thenReturn(Optional.of(user));
        when(portfolioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PortfolioDto created = portfolioService.createPortfolio(dto, "charis");

        assertEquals("Test Portfolio", created.getPortfolioName());
        verify(portfolioRepository).save(any());
    }

    @Test
    void testCreatePortfolio_inactiveUser() {
        User user = new User();
        user.setActive(false);
        when(userRepository.findByUsername("charis")).thenReturn(Optional.of(user));
        PortfolioDto dto = new PortfolioDto(null, "Test", "charis", BigDecimal.TEN, List.of(), null, true);

        assertThrows(RuntimeException.class, () -> portfolioService.createPortfolio(dto, "charis"));
    }

    @Test
    void testUpdatePortfolio_success() {
        User user = new User();
        user.setUsername("charis");

        Portfolio existing = new Portfolio("Old", "charis", BigDecimal.ONE, user);
        existing.setActive(true);

        PortfolioDto update = new PortfolioDto(null, "New", "charis", BigDecimal.TEN, List.of(), null, true);

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(portfolioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PortfolioDto result = portfolioService.updatePortfolio(1L, update, "charis");

        assertEquals("New", result.getPortfolioName());
    }

    @Test
    void testUpdatePortfolio_unauthorized() {
        User user = new User();
        user.setUsername("someone");

        Portfolio existing = new Portfolio("Old", "someone", BigDecimal.ONE, user);
        existing.setActive(true);

        PortfolioDto update = new PortfolioDto(null, "New", "charis", BigDecimal.TEN, List.of(), null, true);

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class, () -> portfolioService.updatePortfolio(1L, update, "charis"));
    }

    @Test
    void testDeactivatePortfolio_asAdmin() {
        Portfolio portfolio = new Portfolio("Port", "admin", BigDecimal.ONE, new User());
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        portfolioService.deactivatePortfolio(1L, "admin", true);

        assertFalse(portfolio.isActive());
        verify(portfolioRepository).save(portfolio);
    }

    @Test
    void testDeactivatePortfolio_unauthorizedUser() {
        Portfolio portfolio = new Portfolio("Port", "owner", BigDecimal.ONE, new User());
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        assertThrows(RuntimeException.class, () -> portfolioService.deactivatePortfolio(1L, "charis", false));
    }

    @Test
    void testFindByOwner() {
        Portfolio portfolio = new Portfolio("Test", "charis", BigDecimal.TEN, new User());
        portfolio.setActive(true);
        when(portfolioRepository.findByOwner("charis")).thenReturn(List.of(portfolio));

        List<PortfolioDto> result = portfolioService.findPortfoliosByOwner("charis");

        assertEquals(1, result.size());
    }

    @Test
    void testFindByTotalValue() {
        Portfolio portfolio = new Portfolio("Big", "owner", BigDecimal.valueOf(500), new User());
        portfolio.setActive(true);
        when(portfolioRepository.findByTotalValueGreaterThan(BigDecimal.valueOf(100)))
                .thenReturn(List.of(portfolio));

        List<PortfolioDto> result = portfolioService.findPortfoliosByTotalValueGreaterThan(BigDecimal.valueOf(100));

        assertEquals(1, result.size());
    }

    @Test
    void testFindByNameContaining() {
        Portfolio portfolio = new Portfolio("Growth Fund", "owner", BigDecimal.ONE, new User());
        portfolio.setActive(true);
        when(portfolioRepository.findByPortfolioNameContaining("Growth"))
                .thenReturn(List.of(portfolio));

        List<PortfolioDto> result = portfolioService.findPortfoliosByNameContaining("Growth");

        assertEquals(1, result.size());
    }
}
