package com.daw.dawCasinoBack.infrastructure.controllers.dtos;

public class StartGameRequest {
    private Long userId;
    private Double betAmount;

    public StartGameRequest() {}

    public StartGameRequest(Long userId, Double betAmount) {
        this.userId = userId;
        this.betAmount = betAmount;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getBetAmount() { return betAmount; }
    public void setBetAmount(Double betAmount) { this.betAmount = betAmount; }
}