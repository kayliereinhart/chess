package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import gamerequest.CreateGameRequest;
import gameresult.ListGamesResult;
import io.javalin.http.BadRequestResponse;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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

    public ListGamesResult listGames() {
        return new ListGamesResult(new ArrayList<>(gameDAO.listGames()));
    }

    public void clear() {
        gameDAO.clearGames();
    }
}
