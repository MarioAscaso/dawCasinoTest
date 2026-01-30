package com.daw.dawCasinoBack.infrastructure.controllers.dtos;

import java.time.LocalDateTime;

public class GameHistoryResponse {
    private String id;
    private String status;
    private Double betAmount;
    private Double resultAmount; // Cuánto ganó/perdió neto
    private LocalDateTime date;

    public GameHistoryResponse(String id, String status, Double betAmount, LocalDateTime date) {
        this.id = id;
        this.status = status;
        this.betAmount = betAmount;
        this.date = date;
        this.resultAmount = calculateResult(status, betAmount);
    }

    private Double calculateResult(String status, Double bet) {
        if ("PLAYER_WINS".equals(status)) return bet; // Ganancia neta
        if ("DEALER_WINS".equals(status)) return -bet; // Pérdida
        return 0.0; // Empate
    }

    // Getters
    public String getId() { return id; }
    public String getStatus() { return status; }
    public Double getBetAmount() { return betAmount; }
    public Double getResultAmount() { return resultAmount; }
    public LocalDateTime getDate() { return date; }
}