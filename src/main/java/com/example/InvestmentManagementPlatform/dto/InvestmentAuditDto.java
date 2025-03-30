package com.example.InvestmentManagementPlatform.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvestmentAuditDto {

    private Long id;
    private Long investmentId;
    private String changeType;
    private LocalDateTime changeDate;
    private BigDecimal oldValue;
    private BigDecimal newValue;

    public InvestmentAuditDto() {
    }

    public InvestmentAuditDto(Long id, Long investmentId, String changeType, LocalDateTime changeDate, BigDecimal oldValue, BigDecimal newValue) {
        this.id = id;
        this.investmentId = investmentId;
        this.changeType = changeType;
        this.changeDate = changeDate;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Long getId() {
        return id;
    }

    public Long getInvestmentId() {
        return investmentId;
    }

    public String getChangeType() {
        return changeType;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public BigDecimal getOldValue() {
        return oldValue;
    }

    public BigDecimal getNewValue() {
        return newValue;
    }
}
