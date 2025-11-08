package model;

import chess.ChessGame;

public record GameData(Integer gameID, String whiteUsername, String blackUsername,
                       String gameName, ChessGame game) {

    public GameData addID(int id) {
        return new GameData(id, whiteUsername, blackUsername, gameName, game);
    }
}
