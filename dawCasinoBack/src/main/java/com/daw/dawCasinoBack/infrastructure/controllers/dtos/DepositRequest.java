package com.daw.dawCasinoBack.infrastructure.controllers.dtos;

public class DepositRequest {
    private Long userId;
    private Double amount;

    public DepositRequest() {}

    public DepositRequest(Long userId, Double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}