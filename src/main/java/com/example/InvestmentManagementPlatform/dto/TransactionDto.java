package com.example.InvestmentManagementPlatform.dto;

import com.example.InvestmentManagementPlatform.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDto {

    private Long id;
    private Long investmentId;
    private LocalDate transactionDate;
    private TransactionType transactionType;
    private BigDecimal amount;

    public TransactionDto() {
    }

    public TransactionDto(Long id, Long investmentId, LocalDate transactionDate, TransactionType transactionType, BigDecimal amount) {
        this.id = id;
        this.investmentId = investmentId;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public Long getInvestmentId() {
        return investmentId;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
