package com.example.InvestmentManagementPlatform.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Data Transfer Object for Investment.
 */
public class InvestmentDto {

    private final Long id;

    @NotBlank(message = "Investment name cannot be blank")
    @Size(max = 100, message = "Investment name must be less than 100 characters")
    private final String investmentName;

    @NotBlank(message = "Investment type cannot be blank")
    @Size(max = 50, message = "Investment type must be less than 50 characters")
    private final String investmentType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be non-negative")
    private final BigDecimal amount;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date must be in the past or today")
    private final LocalDate purchaseDate;

    @PastOrPresent(message = "Sell date must be in the past or today")
    private final LocalDate sellDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Current value must be non-negative")
    private final BigDecimal currentValue;

    private final BigDecimal profitLoss;

    private final Long portfolioId;
    private final boolean active;

    public InvestmentDto(Long id, String investmentName, String investmentType, BigDecimal amount,
                         LocalDate purchaseDate, LocalDate sellDate, BigDecimal currentValue, BigDecimal profitLoss,
                         Long portfolioId, boolean active) {
        this.id = id;
        this.investmentName = investmentName;
        this.investmentType = investmentType;
        this.amount = amount;
        this.purchaseDate = purchaseDate;
        this.sellDate = sellDate;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.portfolioId = portfolioId;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getInvestmentName() {
        return investmentName;
    }

    public String getInvestmentType() {
        return investmentType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getSellDate() {
        return sellDate;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvestmentDto that = (InvestmentDto) o;
        return active == that.active &&
                Objects.equals(id, that.id) &&
                Objects.equals(investmentName, that.investmentName) &&
                Objects.equals(investmentType, that.investmentType) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(purchaseDate, that.purchaseDate) &&
                Objects.equals(sellDate, that.sellDate) &&
                Objects.equals(currentValue, that.currentValue) &&
                Objects.equals(profitLoss, that.profitLoss) &&
                Objects.equals(portfolioId, that.portfolioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, investmentName, investmentType, amount, purchaseDate, sellDate, currentValue, profitLoss, portfolioId, active);
    }

    @Override
    public String toString() {
        return "InvestmentDto{" +
                "id=" + id +
                ", investmentName='" + investmentName + '\'' +
                ", investmentType='" + investmentType + '\'' +
                ", amount=" + amount +
                ", purchaseDate=" + purchaseDate +
                ", sellDate=" + sellDate +
                ", currentValue=" + currentValue +
                ", profitLoss=" + profitLoss +
                ", portfolioId=" + portfolioId +
                ", active=" + active +
                '}';
    }
}
