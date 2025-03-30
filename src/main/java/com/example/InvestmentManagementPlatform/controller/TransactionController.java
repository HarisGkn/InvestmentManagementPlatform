package com.example.InvestmentManagementPlatform.controller;

import com.example.InvestmentManagementPlatform.dto.TransactionDto;
import com.example.InvestmentManagementPlatform.service.TransactionService;
import com.example.InvestmentManagementPlatform.util.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "Get transactions for the logged-in user", description = "Admins see all, users see only their own")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")})
    public ResponseEntity<List<TransactionDto>> getAllTransactions(Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<TransactionDto> transactions = transactionService.getAllTransactions(username, isAdmin)
                .stream()
                .map(Mapper::toTransactionDto)
                .collect(Collectors.toList());

        return transactions.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(transactions);
    }
}
