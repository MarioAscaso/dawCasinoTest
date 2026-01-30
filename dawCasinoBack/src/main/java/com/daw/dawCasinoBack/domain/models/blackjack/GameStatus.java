package com.daw.dawCasinoBack.domain.models.blackjack;

public enum GameStatus {
    PLAYING,        // Partida en curso (Turno del jugador)
    PLAYER_WINS,    // Jugador gana
    DEALER_WINS,    // Banca gana
    DRAW            // Empate (Push)
}