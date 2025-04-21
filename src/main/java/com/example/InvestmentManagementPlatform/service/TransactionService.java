package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.model.Investment;
import com.example.InvestmentManagementPlatform.model.Transaction;
import com.example.InvestmentManagementPlatform.model.TransactionType;
import com.example.InvestmentManagementPlatform.repository.InvestmentRepository;
import com.example.InvestmentManagementPlatform.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final InvestmentRepository investmentRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              InvestmentRepository investmentRepository) {
        this.transactionRepository = transactionRepository;
        this.investmentRepository = investmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions(String username, boolean isAdmin) {
        if (isAdmin) {
            return transactionRepository.findAll();
        }
        // For regular users, only include transactions from investments they own
        return transactionRepository.findAll().stream()
                .filter(tx -> tx.getInvestment()
                        .getPortfolio()
                        .getUser()
                        .getUsername()
                        .equals(username))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByInvestment(Long investmentId) {
        return transactionRepository.findByInvestmentId(investmentId);
    }

    @Transactional
    public Transaction createTransaction(Long investmentId, LocalDate transactionDate,
                                         TransactionType transactionType, BigDecimal amount, String username) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new RuntimeException("Investment not found with id " + investmentId));
        // Verify the investment belongs to the current user (prevent unauthorized access)
        if (!investment.getPortfolio().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized: You cannot add transactions to this investment.");
        }
        Transaction transaction = new Transaction(investment, transactionDate, transactionType, amount);
        return transactionRepository.save(transaction);
    }
}
