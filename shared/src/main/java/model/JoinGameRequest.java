package model;

import chess.ChessGame;

public record JoinGameRequest(String username, ChessGame.TeamColor playerColor, int gameID) {

    public JoinGameRequest addUsername(String username) {
        return new JoinGameRequest(username, playerColor, gameID);
    }
}
