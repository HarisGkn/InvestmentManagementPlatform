package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.model.Investment;
import com.example.InvestmentManagementPlatform.model.Portfolio;
import com.example.InvestmentManagementPlatform.model.Transaction;
import com.example.InvestmentManagementPlatform.model.TransactionType;
import com.example.InvestmentManagementPlatform.model.User;
import com.example.InvestmentManagementPlatform.repository.InvestmentRepository;
import com.example.InvestmentManagementPlatform.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Investment investment;
    private Portfolio portfolio;
    private User user;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("testuser");

        portfolio = new Portfolio();
        portfolio.setUser(user);

        investment = new Investment();
        investment.setPortfolio(portfolio);

        Field idField = Investment.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(investment, 1L);
    }

    @Test
    void testCreateTransaction_Success() {
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        Transaction expected = new Transaction(investment, LocalDate.now(), TransactionType.BUY, BigDecimal.TEN);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expected);

        Transaction result = transactionService.createTransaction(
                1L,
                LocalDate.now(),
                TransactionType.BUY,
                BigDecimal.TEN,
                "testuser"
        );

        assertNotNull(result);
        assertEquals(TransactionType.BUY, result.getTransactionType());
    }

    @Test
    void testCreateTransaction_InvestmentNotFound() {
        when(investmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(1L, LocalDate.now(), TransactionType.SELL, BigDecimal.TEN, "testuser"));
    }

    @Test
    void testCreateTransaction_UnauthorizedUser() {
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(1L, LocalDate.now(), TransactionType.SELL, BigDecimal.TEN, "otheruser"));
    }

    @Test
    void testGetAllTransactions_Admin() {
        Transaction tx1 = new Transaction();
        when(transactionRepository.findAll()).thenReturn(List.of(tx1));

        List<Transaction> result = transactionService.getAllTransactions("admin", true);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllTransactions_User() {
        Transaction tx = new Transaction();
        tx.setInvestment(investment);
        when(transactionRepository.findAll()).thenReturn(List.of(tx));

        List<Transaction> result = transactionService.getAllTransactions("testuser", false);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllTransactions_UserFilteredOut() {
        Transaction tx = new Transaction();
        tx.setInvestment(investment);
        when(transactionRepository.findAll()).thenReturn(List.of(tx));

        List<Transaction> result = transactionService.getAllTransactions("wronguser", false);

        assertEquals(0, result.size());
    }

    @Test
    void testGetTransactionsByInvestment() {
        Transaction tx = new Transaction();
        when(transactionRepository.findByInvestmentId(1L)).thenReturn(List.of(tx));

        List<Transaction> result = transactionService.getTransactionsByInvestment(1L);

        assertEquals(1, result.size());
    }
}
