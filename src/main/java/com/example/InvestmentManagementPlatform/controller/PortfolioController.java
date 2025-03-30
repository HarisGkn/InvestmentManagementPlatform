package com.example.InvestmentManagementPlatform.controller;

import com.example.InvestmentManagementPlatform.dto.PortfolioDto;
import com.example.InvestmentManagementPlatform.service.PortfolioService;
import com.example.InvestmentManagementPlatform.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<List<PortfolioDto>> getAllPortfolios(Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<PortfolioDto> portfolios = portfolioService.getAllPortfolios(username, isAdmin);

        return portfolios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(portfolios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDto> getPortfolioById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return portfolioService.getPortfolioById(id, username, isAdmin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PortfolioDto> createPortfolio(@RequestBody PortfolioDto portfolioDto, Authentication authentication) {
        PortfolioDto savedPortfolio = portfolioService.createPortfolio(portfolioDto, authentication.getName());
        return ResponseEntity.ok(savedPortfolio);
    }


    @PutMapping("/{id}")
    public ResponseEntity<PortfolioDto> updatePortfolio(@PathVariable Long id, @RequestBody PortfolioDto portfolioDto, Authentication authentication) {
        PortfolioDto updatedPortfolio = portfolioService.updatePortfolio(id, portfolioDto, authentication.getName());
        return ResponseEntity.ok(updatedPortfolio);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePortfolio(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        portfolioService.deactivatePortfolio(id, authentication.getName(), isAdmin);
        return ResponseEntity.noContent().build();
    }
}
