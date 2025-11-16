package service;

import java.util.ArrayList;

import dataaccess.DataAccessException;
import dataaccess.SQLGameDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.CreateGameRequest;
import model.JoinGameRequest;
import model.ListGamesResult;
import model.GameData;

public class GameService {

    private final GameDAO gameDAO;

    public GameService() throws DataAccessException{
        gameDAO = new SQLGameDAO();
    }

    public Integer createGame(CreateGameRequest request) throws DataAccessException {
        if (request.gameName() == null) {
            throw new BadRequestResponse("bad request");
        }
        GameData gameData = new GameData(null, null, null, request.gameName(), new ChessGame());
        return gameDAO.createGame(gameData);
    }

    public ListGamesResult listGames() throws DataAccessException {
        return new ListGamesResult(new ArrayList<>(gameDAO.listGames()));
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException {
        GameData gameData = gameDAO.getGame(request.gameID());

        if (gameData == null || (request.playerColor() != ChessGame.TeamColor.BLACK &&
                request.playerColor() != ChessGame.TeamColor.WHITE)) {
            throw new BadRequestResponse("invalid game ID");
        } else if ((request.playerColor() == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null) ||
                (request.playerColor() == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null)) {
            throw new ForbiddenResponse("player color already taken");
        }

        try {
            gameDAO.addPlayer(request.username(), request.playerColor(), request.gameID());
        } catch (DataAccessException e) {
            throw new BadRequestResponse("invalid username, player color, or game ID");
        }
    }

    public void clear() throws DataAccessException {
        gameDAO.clearGames();
    }
}
