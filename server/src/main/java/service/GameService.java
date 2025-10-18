package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import gamerequest.CreateGameRequest;
import model.GameData;

public class GameService {

    private final GameDAO gameDAO = new MemoryGameDAO();

    public Integer createGame(CreateGameRequest request) {
        int gameID = gameDAO.findNextID();
        GameData gameData = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(gameData);

        return gameID;
    }
}
