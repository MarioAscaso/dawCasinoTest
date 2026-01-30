package com.daw.dawCasinoBack.domain.models.blackjack;

import java.util.UUID;

public class BlackJackGame {

    private String id;
    private Long userId;
    private Deck deck;          // <--- Jackson necesita acceder a esto
    private Hand playerHand;
    private Hand dealerHand;
    private GameStatus status;
    private Double betAmount;

    // 1. Constructor vacío PÚBLICO (Obligatorio para Jackson)
    public BlackJackGame() {}

    // 2. Constructor para empezar partida
    public BlackJackGame(Long userId, Double betAmount) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.betAmount = betAmount;

        // ¡IMPORTANTE! Creamos la baraja LLENA (true)
        this.deck = new Deck(true);
        this.deck.shuffle();

        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.status = GameStatus.PLAYING;

        dealInitialCards();
    }

    // --- LÓGICA DEL JUEGO ---

    private void dealInitialCards() {
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        checkInitialWinCondition();
    }

    private void checkInitialWinCondition() {
        if (playerHand.isBlackJack() && !dealerHand.isBlackJack()) {
            status = GameStatus.PLAYER_WINS;
        } else if (!playerHand.isBlackJack() && dealerHand.isBlackJack()) {
            status = GameStatus.DEALER_WINS;
        } else if (playerHand.isBlackJack() && dealerHand.isBlackJack()) {
            status = GameStatus.DRAW;
        }
    }

    public void playerHit() {
        if (status != GameStatus.PLAYING) return;

        playerHand.addCard(deck.drawCard());

        if (playerHand.isBusted()) {
            status = GameStatus.DEALER_WINS;
        }
    }

    public void playerStand() {
        if (status != GameStatus.PLAYING) return;

        dealerTurn();
        resolveWinner();
    }

    private void dealerTurn() {
        // La banca pide carta obligatoriamente hasta llegar a 17
        while (dealerHand.calculateScore() < 17) {
            dealerHand.addCard(deck.drawCard());
        }
    }

    private void resolveWinner() {
        int playerScore = playerHand.calculateScore();
        int dealerScore = dealerHand.calculateScore();

        if (dealerHand.isBusted()) {
            status = GameStatus.PLAYER_WINS;
        } else if (playerScore > dealerScore) {
            status = GameStatus.PLAYER_WINS;
        } else if (dealerScore > playerScore) {
            status = GameStatus.DEALER_WINS;
        } else {
            status = GameStatus.DRAW;
        }
    }

    // --- GETTERS Y SETTERS (VITALES PARA JSON) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Deck getDeck() { return deck; }
    public void setDeck(Deck deck) { this.deck = deck; }

    public Hand getPlayerHand() { return playerHand; }
    public void setPlayerHand(Hand playerHand) { this.playerHand = playerHand; }

    public Hand getDealerHand() { return dealerHand; }
    public void setDealerHand(Hand dealerHand) { this.dealerHand = dealerHand; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public Double getBetAmount() { return betAmount; }
    public void setBetAmount(Double betAmount) { this.betAmount = betAmount; }
}