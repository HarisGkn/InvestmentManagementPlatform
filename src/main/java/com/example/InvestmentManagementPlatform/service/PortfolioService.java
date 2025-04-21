package com.example.InvestmentManagementPlatform.service;

import com.example.InvestmentManagementPlatform.dto.PortfolioDto;
import com.example.InvestmentManagementPlatform.model.Portfolio;
import com.example.InvestmentManagementPlatform.model.User;
import com.example.InvestmentManagementPlatform.repository.PortfolioRepository;
import com.example.InvestmentManagementPlatform.repository.UserRepository;
import com.example.InvestmentManagementPlatform.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> getAllPortfolios(String username, boolean isAdmin) {
        List<Portfolio> portfolios;

        if (isAdmin) {
            portfolios = portfolioRepository.findByActiveTrue();
        } else {
            portfolios = portfolioRepository.findByUser_UsernameAndActiveTrue(username);
        }

        return portfolios.stream()
                .map(Mapper::toPortfolioDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Optional<PortfolioDto> getPortfolioById(Long id, String username, boolean isAdmin) {
        return portfolioRepository.findById(id)
                .filter(portfolio -> isAdmin || portfolio.getUser().getUsername().equals(username))
                .filter(Portfolio::isActive) // portfolio must be active
                .map(Mapper::toPortfolioDto);
    }

    @Transactional
    public PortfolioDto createPortfolio(PortfolioDto portfolioDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!user.isActive()) {
            throw new RuntimeException("Cannot create portfolio for an inactive user");
        }
        // Use the current username as the portfolio owner
        Portfolio portfolio = new Portfolio(portfolioDto.getPortfolioName(), username, portfolioDto.getTotalValue(), user);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return Mapper.toPortfolioDto(savedPortfolio);
    }


    @Transactional
    public PortfolioDto updatePortfolio(Long id, PortfolioDto portfolioDto, String username) {
        return portfolioRepository.findById(id).map(portfolio -> {
            // Ensure the current user owns this portfolio
            if (!portfolio.getUser().getUsername().equals(username)) {
                throw new RuntimeException("Unauthorized access to this portfolio");
            }
            // Cannot update if the portfolio is inactive
            if (!portfolio.isActive()) {
                throw new RuntimeException("Cannot update an inactive portfolio");
            }
            portfolio.setPortfolioName(portfolioDto.getPortfolioName());
            portfolio.setOwner(portfolioDto.getOwner());
            portfolio.setTotalValue(portfolioDto.getTotalValue());
            Portfolio updatedPortfolio = portfolioRepository.save(portfolio);
            return Mapper.toPortfolioDto(updatedPortfolio);
        }).orElseThrow(() -> new RuntimeException("Portfolio not found with id " + id));
    }

    @Transactional
    public void deactivatePortfolio(Long id, String username, boolean isAdmin) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id " + id));
        // Only admin or the owner can deactivate this portfolio
        if (!isAdmin && !portfolio.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to deactivate this portfolio");
        }
        portfolio.setActive(false);
        portfolioRepository.save(portfolio);
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> findPortfoliosByOwner(String owner) {
        return portfolioRepository.findByOwner(owner).stream()
                .filter(Portfolio::isActive)
                .map(Mapper::toPortfolioDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> findPortfoliosByTotalValueGreaterThan(BigDecimal value) {
        return portfolioRepository.findByTotalValueGreaterThan(value).stream()
                .filter(Portfolio::isActive)
                .map(Mapper::toPortfolioDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> findPortfoliosByNameContaining(String keyword) {
        return portfolioRepository.findByPortfolioNameContaining(keyword).stream()
                .filter(Portfolio::isActive)
                .map(Mapper::toPortfolioDto)
                .collect(Collectors.toList());
    }
}
