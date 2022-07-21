package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.RestResponseDto;
import com.binaracademy.secondhand.service.UserTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserTransactionController {

    @Autowired
    private final UserTransactionService userTransactionService;

    @GetMapping("/transaction-history/buyer")
    public ResponseEntity<RestResponseDto> getBuyerTransactions(Authentication authentication) {
        return ResponseEntity.ok(
            new RestResponseDto(200, "ok", userTransactionService.getBuyerTransactions(authentication.getPrincipal().toString()))
        );
    }

    @GetMapping("/transaction-history/seller")
    public ResponseEntity<RestResponseDto> getSellerTransactions(Authentication authentication) {
        return ResponseEntity.ok(
            new RestResponseDto(200, "ok", userTransactionService.getSellerTransactions(authentication.getPrincipal().toString()))
        );
    }
}
