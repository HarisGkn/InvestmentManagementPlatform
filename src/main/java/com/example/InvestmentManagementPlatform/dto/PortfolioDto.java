package com.example.InvestmentManagementPlatform.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class PortfolioDto {

    private final Long id;

    @NotBlank(message = "Portfolio name cannot be blank")
    @Size(max = 100, message = "Portfolio name must be less than 100 characters")
    private final String portfolioName;

    @NotBlank(message = "Owner name cannot be blank")
    @Size(max = 100, message = "Owner name must be less than 100 characters")
    private final String owner;

    @NotNull(message = "Total value is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total value must be non-negative")
    private final BigDecimal totalValue;

    private final List<Long> investmentIds;
    private final Long userId;
    private final boolean active;

    public PortfolioDto(Long id, String portfolioName, String owner, BigDecimal totalValue,
                        List<Long> investmentIds, Long userId, boolean active) {
        this.id = id;
        this.portfolioName = portfolioName;
        this.owner = owner;
        this.totalValue = totalValue;
        this.investmentIds = investmentIds;
        this.userId = userId;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public List<Long> getInvestmentIds() {
        return investmentIds;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioDto that = (PortfolioDto) o;
        return active == that.active &&
                Objects.equals(id, that.id) &&
                Objects.equals(portfolioName, that.portfolioName) &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(totalValue, that.totalValue) &&
                Objects.equals(investmentIds, that.investmentIds) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, portfolioName, owner, totalValue, investmentIds, userId, active);
    }

    @Override
    public String toString() {
        return "PortfolioDto{" +
                "id=" + id +
                ", portfolioName='" + portfolioName + '\'' +
                ", owner='" + owner + '\'' +
                ", totalValue=" + totalValue +
                ", investmentIds=" + investmentIds +
                ", userId=" + userId +
                ", active=" + active +
                '}';
    }
}
