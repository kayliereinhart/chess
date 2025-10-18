package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import gamerequest.CreateGameRequest;
import model.GameData;

public class GameService {

    private final GameDAO gameDAO = new MemoryGameDAO();

    public GameData createGame(CreateGameRequest request) {
        GameData gameData = new GameData(1, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(gameData);

        return gameData;
    }
}
