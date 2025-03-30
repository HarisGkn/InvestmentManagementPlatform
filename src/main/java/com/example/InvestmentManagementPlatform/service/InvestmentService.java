package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.model.Investment;
import com.example.InvestmentManagementPlatform.model.Portfolio;
import com.example.InvestmentManagementPlatform.model.TransactionType;
import com.example.InvestmentManagementPlatform.repository.InvestmentRepository;
import com.example.InvestmentManagementPlatform.repository.PortfolioRepository;
import com.example.InvestmentManagementPlatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final InvestmentAuditService investmentAuditService;

    @Autowired
    public InvestmentService(InvestmentRepository investmentRepository,
                             PortfolioRepository portfolioRepository,
                             UserRepository userRepository,
                             TransactionService transactionService,
                             InvestmentAuditService investmentAuditService) {
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.investmentAuditService = investmentAuditService;
    }

    @Transactional(readOnly = true)
    public List<Investment> getAllInvestments(String username, boolean isAdmin) {
        return isAdmin
                ? investmentRepository.findAll()
                : investmentRepository.findInvestmentsByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Investment> getInvestmentById(Long id) {
        return investmentRepository.findById(id);
    }

    @Transactional
    public Investment createInvestment(Investment investment, String username) {
        if (investment.getPortfolio() == null || investment.getPortfolio().getId() == null) {
            throw new RuntimeException("Portfolio ID must be provided.");
        }
        Portfolio portfolio = portfolioRepository.findById(investment.getPortfolio().getId())
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        if (!portfolio.getUser().getUsername().equals(username) || !portfolio.getUser().isActive()) {
            throw new RuntimeException("Unauthorized: You cannot add investments to this portfolio.");
        }
        investment.setActive(true);
        investment.setPortfolio(portfolio);
        Investment savedInvestment = investmentRepository.save(investment);
        transactionService.createTransaction(
                savedInvestment.getId(),
                savedInvestment.getPurchaseDate(),
                TransactionType.BUY,
                savedInvestment.getAmount(),
                username
        );
        return savedInvestment;
    }

    @Transactional
    public Investment updateInvestment(Long id, Investment investmentDetails, String username, boolean isAdmin) {
        return investmentRepository.findById(id).map(existing -> {
            if (!isAdmin && !existing.getPortfolio().getUser().getUsername().equals(username)) {
                throw new RuntimeException("Unauthorized: You cannot modify this investment.");
            }

            // Track changes
            logIfChanged(existing, investmentDetails);

            existing.setInvestmentName(investmentDetails.getInvestmentName());
            existing.setInvestmentType(investmentDetails.getInvestmentType());
            existing.setAmount(investmentDetails.getAmount());
            existing.setPurchaseDate(investmentDetails.getPurchaseDate());
            existing.setSellDate(investmentDetails.getSellDate());
            existing.setCurrentValue(investmentDetails.getCurrentValue());
            existing.setProfitLoss(investmentDetails.getProfitLoss());

            return investmentRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Investment not found with id " + id));
    }

    private void logIfChanged(Investment oldInv, Investment newInv) {
        Long id = oldInv.getId();

        if (!equals(oldInv.getAmount(), newInv.getAmount())) {
            investmentAuditService.logInvestmentChange(id, "Amount Updated", oldInv.getAmount(), newInv.getAmount());
        }

        if (!equals(oldInv.getCurrentValue(), newInv.getCurrentValue())) {
            investmentAuditService.logInvestmentChange(id, "Current Value Updated", oldInv.getCurrentValue(), newInv.getCurrentValue());
        }

        if (!equals(oldInv.getProfitLoss(), newInv.getProfitLoss())) {
            investmentAuditService.logInvestmentChange(id, "Profit/Loss Updated", oldInv.getProfitLoss(), newInv.getProfitLoss());
        }

        if (!equalsDate(oldInv.getPurchaseDate(), newInv.getPurchaseDate())) {
            investmentAuditService.logInvestmentChange(id, "Purchase Date Updated", null, null);
        }

        if (!equalsDate(oldInv.getSellDate(), newInv.getSellDate())) {
            investmentAuditService.logInvestmentChange(id, "Sell Date Updated", null, null);
        }

        if (!equalsString(oldInv.getInvestmentName(), newInv.getInvestmentName())) {
            investmentAuditService.logInvestmentChange(id, "Investment Name Updated", null, null);
        }

        if (!equalsString(oldInv.getInvestmentType(), newInv.getInvestmentType())) {
            investmentAuditService.logInvestmentChange(id, "Investment Type Updated", null, null);
        }
    }

    private boolean equals(BigDecimal a, BigDecimal b) {
        return (a == null && b == null) || (a != null && a.compareTo(b) == 0);
    }

    private boolean equalsDate(LocalDate a, LocalDate b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    private boolean equalsString(String a, String b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    @Transactional
    public void deactivateInvestment(Long id, String username, boolean isAdmin) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investment not found with id " + id));

        if (!isAdmin && !investment.getPortfolio().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized: You do not own this investment.");
        }

        investment.setActive(false);
        investmentRepository.save(investment);
    }
}
