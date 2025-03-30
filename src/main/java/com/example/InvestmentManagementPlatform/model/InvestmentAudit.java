package com.example.InvestmentManagementPlatform.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_audit")
public class InvestmentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "investment_id", nullable = false)
    private Investment investment;

    @Column(nullable = false)
    private String changeType;

    @Column(nullable = false)
    private LocalDateTime changeDate;

    private BigDecimal oldValue;
    private BigDecimal newValue;

    @Column(nullable = false)
    private boolean active = true;

    public InvestmentAudit() {}

    public InvestmentAudit(Investment investment, String changeType, LocalDateTime changeDate, BigDecimal oldValue, BigDecimal newValue) {
        this.investment = investment;
        this.changeType = changeType;
        this.changeDate = changeDate;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public Investment getInvestment() {
        return investment;
    }

    public void setInvestment(Investment investment) {
        this.investment = investment;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public BigDecimal getOldValue() {
        return oldValue;
    }

    public void setOldValue(BigDecimal oldValue) {
        this.oldValue = oldValue;
    }

    public BigDecimal getNewValue() {
        return newValue;
    }

    public void setNewValue(BigDecimal newValue) {
        this.newValue = newValue;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
