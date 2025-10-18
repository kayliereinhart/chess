package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import gamerequest.CreateGameRequest;
import gamerequest.JoinGameRequest;
import gameresult.ListGamesResult;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
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

    public void joinGame(JoinGameRequest request) {
        GameData gameData = gameDAO.getGame(request.gameID());

        if (gameData == null) {
            throw new BadRequestResponse("bad request");
        } else if ((request.playerColor() == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null) ||
                (request.playerColor() == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null)) {
            throw new ForbiddenResponse("already taken");
        } else if (request.playerColor() != ChessGame.TeamColor.BLACK && request.playerColor() != ChessGame.TeamColor.WHITE) {
            throw new BadRequestResponse("bad request");
        }
        gameDAO.addPlayer(request.username(), request.playerColor(), request.gameID());
    }

    public void clear() {
        gameDAO.clearGames();
    }
}
