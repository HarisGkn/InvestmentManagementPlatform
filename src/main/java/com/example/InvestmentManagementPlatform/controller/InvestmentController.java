package com.example.InvestmentManagementPlatform.controller;

import com.example.InvestmentManagementPlatform.dto.InvestmentDto;
import com.example.InvestmentManagementPlatform.model.Investment;
import com.example.InvestmentManagementPlatform.model.Portfolio;
import com.example.InvestmentManagementPlatform.repository.PortfolioRepository;
import com.example.InvestmentManagementPlatform.service.InvestmentService;
import com.example.InvestmentManagementPlatform.util.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    private final InvestmentService investmentService;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public InvestmentController(InvestmentService investmentService, PortfolioRepository portfolioRepository) {
        this.investmentService = investmentService;
        this.portfolioRepository = portfolioRepository;
    }

    @GetMapping
    @Operation(summary = "Get investments for the logged-in user", description = "Retrieve investments based on user role")
    public ResponseEntity<List<InvestmentDto>> getInvestments(Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<InvestmentDto> investments = investmentService.getAllInvestments(username, isAdmin)
                .stream()
                .map(Mapper::toInvestmentDto)
                .collect(Collectors.toList());

        return investments.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(investments);
    }

    @PostMapping
    public ResponseEntity<InvestmentDto> createInvestment(@Valid @RequestBody InvestmentDto investmentDto, Authentication authentication) {
        String username = authentication.getName();
        if (investmentDto.getPortfolioId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Portfolio portfolio = portfolioRepository.findById(investmentDto.getPortfolioId())
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        Investment investment = Mapper.toInvestmentEntity(investmentDto);
        investment.setPortfolio(portfolio);
        Investment createdInvestment = investmentService.createInvestment(investment, username);

        return ResponseEntity.ok(Mapper.toInvestmentDto(createdInvestment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentDto> getInvestmentById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return investmentService.getInvestmentById(id)
                .filter(investment -> isAdmin || investment.getPortfolio().getUser().getUsername().equals(username))
                .map(Mapper::toInvestmentDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestmentDto> updateInvestment(@PathVariable Long id, @Valid @RequestBody InvestmentDto investmentDto, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        Investment investment = Mapper.toInvestmentEntity(investmentDto);
        Investment updatedInvestment = investmentService.updateInvestment(id, investment, username, isAdmin);

        return ResponseEntity.ok(Mapper.toInvestmentDto(updatedInvestment));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateInvestment(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        investmentService.deactivateInvestment(id, username, isAdmin);

        return ResponseEntity.noContent().build();
    }

}
