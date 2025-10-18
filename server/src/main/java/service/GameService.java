package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import gamerequest.CreateGameRequest;
import io.javalin.http.BadRequestResponse;
import model.GameData;

public class GameService {

    private final GameDAO gameDAO = new MemoryGameDAO();

    public Integer createGame(CreateGameRequest request) {
        if (request.gameName() == null) {
            throw new BadRequestResponse("bad request");
        }
        int gameID = gameDAO.findNextID();
        GameData gameData = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(gameData);

        return gameID;
    }

    public void clear() {
        gameDAO.clearGames();
    }
}
