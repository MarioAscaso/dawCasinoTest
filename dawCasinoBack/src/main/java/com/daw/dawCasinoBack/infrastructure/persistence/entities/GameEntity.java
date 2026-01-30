package com.daw.dawCasinoBack.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    private String id; // El UUID de la partida

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String status; // PLAYING, PLAYER_WINS...

    @Column(name = "bet_amount")
    private Double betAmount;

    // 🔥 AQUÍ ESTÁ LA CLAVE: Guardamos todo el objeto BlackJackGame como un texto enorme
    @Lob // Indica que es un objeto grande (Large Object)
    @Column(name = "game_state", columnDefinition = "LONGTEXT")
    private String gameState;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public GameEntity() {}

    public GameEntity(String id, Long userId, String status, Double betAmount, String gameState) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.betAmount = betAmount;
        this.gameState = gameState;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters básicos (Generálos con el IDE)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getBetAmount() { return betAmount; }
    public void setBetAmount(Double betAmount) { this.betAmount = betAmount; }

    public String getGameState() { return gameState; }
    public void setGameState(String gameState) { this.gameState = gameState; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}