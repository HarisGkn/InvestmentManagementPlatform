package com.example.InvestmentManagementPlatform.controller;

import com.example.InvestmentManagementPlatform.dto.DividendDto;
import com.example.InvestmentManagementPlatform.service.DividendService;
import com.example.InvestmentManagementPlatform.util.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dividends")
public class DividendController {

    private final DividendService dividendService;

    @Autowired
    public DividendController(DividendService dividendService) {
        this.dividendService = dividendService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get all dividends", description = "Retrieve all dividend records (filtered by user unless admin)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Dividends retrieved successfully")})
    public ResponseEntity<List<DividendDto>> getAllDividends(Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<DividendDto> dividends = dividendService.getAllDividends(username, isAdmin).stream()
                .map(Mapper::toDividendDto)
                .collect(Collectors.toList());

        return dividends.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dividends);
    }

}
