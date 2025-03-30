package com.example.InvestmentManagementPlatform.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DividendDto {

    private Long id;
    private Long investmentId;
    private LocalDate paymentDate;
    private BigDecimal amount;

    public DividendDto() {
    }

    public DividendDto(Long id, Long investmentId, LocalDate paymentDate, BigDecimal amount) {
        this.id = id;
        this.investmentId = investmentId;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public Long getInvestmentId() {
        return investmentId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
