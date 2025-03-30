package com.example.InvestmentManagementPlatform.controller;

import com.example.InvestmentManagementPlatform.dto.InvestmentAuditDto;
import com.example.InvestmentManagementPlatform.service.InvestmentAuditService;
import com.example.InvestmentManagementPlatform.util.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/investment-audits")
public class InvestmentAuditController {

    private final InvestmentAuditService investmentAuditService;

    @Autowired
    public InvestmentAuditController(InvestmentAuditService investmentAuditService) {
        this.investmentAuditService = investmentAuditService;
    }

    @GetMapping
    @Operation(summary = "Get investment audits", description = "Admins get all audits, users get their own")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audits retrieved successfully")
    })
    public ResponseEntity<List<InvestmentAuditDto>> getAudits(Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<InvestmentAuditDto> audits = (isAdmin
                ? investmentAuditService.getAllAudits()
                : investmentAuditService.getAuditsForUser(username))
                .stream()
                .map(Mapper::toInvestmentAuditDto)
                .collect(Collectors.toList());

        return audits.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(audits);
    }

}
