package com.example.InvestmentManagementPlatform.util;

import com.example.InvestmentManagementPlatform.dto.*;
import com.example.InvestmentManagementPlatform.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class Mapper {

    private Mapper() {
        throw new IllegalStateException("Utility class");
    }

    public static InvestmentDto toInvestmentDto(Investment investment) {
        return new InvestmentDto(
                investment.getId(),
                investment.getInvestmentName(),
                investment.getInvestmentType(),
                investment.getAmount(),
                investment.getPurchaseDate(),
                investment.getSellDate(),
                investment.getCurrentValue(),
                investment.getProfitLoss(),
                (investment.getPortfolio() != null) ? investment.getPortfolio().getId() : null,
                investment.isActive()
        );
    }

    public static Investment toInvestmentEntity(InvestmentDto investmentDto) {
        Investment investment = new Investment();
        investment.setInvestmentName(investmentDto.getInvestmentName());
        investment.setInvestmentType(investmentDto.getInvestmentType());
        investment.setAmount(investmentDto.getAmount());
        investment.setPurchaseDate(investmentDto.getPurchaseDate());
        investment.setSellDate(investmentDto.getSellDate());
        investment.setCurrentValue(investmentDto.getCurrentValue());
        investment.setProfitLoss(investmentDto.getProfitLoss());
        investment.setActive(investmentDto.isActive());
        return investment;
    }

    public static PortfolioDto toPortfolioDto(Portfolio portfolio) {
        List<Long> investmentIds = (portfolio.getInvestments() != null)
                ? portfolio.getInvestments().stream()
                .map(Investment::getId)
                .collect(Collectors.toList())
                : null;

        return new PortfolioDto(
                portfolio.getId(),
                portfolio.getPortfolioName(),
                portfolio.getOwner(),
                portfolio.getTotalValue(),
                investmentIds,
                (portfolio.getUser() != null) ? portfolio.getUser().getId() : null,
                portfolio.isActive()
        );
    }

    public static Portfolio toPortfolioEntity(PortfolioDto portfolioDto, User user) {
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioName(portfolioDto.getPortfolioName());
        portfolio.setOwner(portfolioDto.getOwner());
        portfolio.setTotalValue(portfolioDto.getTotalValue());
        portfolio.setUser(user);
        portfolio.setActive(portfolioDto.isActive());
        return portfolio;
    }

    public static InvestmentAuditDto toInvestmentAuditDto(InvestmentAudit audit) {
        return new InvestmentAuditDto(
                audit.getId(),
                audit.getInvestment().getId(),
                audit.getChangeType(),
                audit.getChangeDate(),
                audit.getOldValue(),
                audit.getNewValue()
        );
    }

    public static TransactionDto toTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getInvestment().getId(),
                transaction.getTransactionDate(),
                transaction.getTransactionType(),
                transaction.getAmount()
        );
    }

    public static DividendDto toDividendDto(Dividend dividend) {
        return new DividendDto(
                dividend.getId(),
                dividend.getInvestment().getId(),
                dividend.getPaymentDate(),
                dividend.getAmount()
        );
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getUsername(), user.getRole(), user.isActive());
    }

    public static UserRegistrationDto toUserRegistrationDto(User user) {
        return new UserRegistrationDto(user.getUsername(), "", user.getRole(), user.isActive());
    }

}
