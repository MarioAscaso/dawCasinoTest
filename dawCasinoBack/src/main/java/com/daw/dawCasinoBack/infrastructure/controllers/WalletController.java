package com.daw.dawCasinoBack.infrastructure.controllers;

import com.daw.dawCasinoBack.application.usecases.DepositMoneyUseCase;
import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.infrastructure.controllers.dtos.DepositRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    private final DepositMoneyUseCase depositMoneyUseCase;

    public WalletController(DepositMoneyUseCase depositMoneyUseCase) {
        this.depositMoneyUseCase = depositMoneyUseCase;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody DepositRequest request) {
        try {
            User updatedUser = depositMoneyUseCase.deposit(request.getUserId(), request.getAmount());
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}