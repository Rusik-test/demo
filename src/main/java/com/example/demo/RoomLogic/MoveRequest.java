package com.example.demo.RoomLogic;

public class MoveRequest {
    private String gameId;
    private String playerName;
    private Move move;
    private String PlayerColor;
    public MoveRequest() {}

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public Move getMove() { return move; }
    public void setMove(Move move) { this.move = move; }

    public String getPlayerColor() {
        return PlayerColor;
    }

    public void setPlayerColor(String playerColor) {
        PlayerColor = playerColor;
    }
}
