package com.daw.dawCasinoBack.infrastructure.controllers.dtos;

import com.daw.dawCasinoBack.domain.models.blackjack.GameStatus;
import com.daw.dawCasinoBack.domain.models.blackjack.Hand;

public class GameResponse {

    private String id;
    private GameStatus status;
    private Hand playerHand;
    private Hand dealerHand;
    private Double betAmount;

    // Constructor
    public GameResponse(String id, GameStatus status, Hand playerHand, Hand dealerHand, Double betAmount) {
        this.id = id;
        this.status = status;
        this.playerHand = playerHand;
        this.dealerHand = dealerHand;
        this.betAmount = betAmount;
    }

    // Getters (necesarios para que se convierta a JSON)
    public String getId() { return id; }
    public GameStatus getStatus() { return status; }
    public Hand getPlayerHand() { return playerHand; }
    public Hand getDealerHand() { return dealerHand; }
    public Double getBetAmount() { return betAmount; }
}