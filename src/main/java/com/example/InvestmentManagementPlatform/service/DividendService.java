package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.model.Dividend;
import com.example.InvestmentManagementPlatform.model.Investment;
import com.example.InvestmentManagementPlatform.repository.DividendRepository;
import com.example.InvestmentManagementPlatform.repository.InvestmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DividendService {

    private final DividendRepository dividendRepository;
    private final InvestmentRepository investmentRepository;

    @Autowired
    public DividendService(DividendRepository dividendRepository, InvestmentRepository investmentRepository) {
        this.dividendRepository = dividendRepository;
        this.investmentRepository = investmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Dividend> getAllDividends(String username, boolean isAdmin) {
        if (isAdmin) {
            return dividendRepository.findAll();
        }

        return dividendRepository.findAll().stream()
                .filter(div -> div.getInvestment()
                        .getPortfolio()
                        .getUser()
                        .getUsername()
                        .equals(username))
                .toList();
    }


    public List<Dividend> getDividendsByInvestment(Long investmentId) {
        return dividendRepository.findByInvestmentId(investmentId);
    }

    @Transactional
    public Dividend createDividend(Long investmentId, LocalDate paymentDate, BigDecimal amount) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new RuntimeException("Investment not found with id " + investmentId));

        Dividend dividend = new Dividend(investment, paymentDate, amount);
        return dividendRepository.save(dividend);
    }
}
