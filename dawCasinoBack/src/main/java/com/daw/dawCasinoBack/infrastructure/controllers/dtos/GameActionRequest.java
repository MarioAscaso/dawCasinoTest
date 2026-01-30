package com.daw.dawCasinoBack.infrastructure.controllers.dtos;

public class GameActionRequest {
    private String gameId;
    private String action;

    public GameActionRequest() {}

    public GameActionRequest(String gameId, String action) {
        this.gameId = gameId;
        this.action = action;
    }

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}