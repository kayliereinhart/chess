package handler;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import gsonbuilder.GameGsonBuilder;
import model.CreateGameRequest;
import model.JoinGameRequest;
import service.GameService;

public class GameHandler {

    private final GameService gameService;
    private final Gson serializer;

    public GameHandler() throws DataAccessException {
        gameService = new GameService();
        GameGsonBuilder builder = new GameGsonBuilder();
        serializer = builder.createSerializer();
    }

    public String handleCreate(String requestJson) throws DataAccessException {
        CreateGameRequest request = serializer.fromJson(requestJson, CreateGameRequest.class);
        Integer gameID = gameService.createGame(request);

        return serializer.toJson(Map.of("gameID", gameID));
    }

    public String handleList() throws DataAccessException {
        return serializer.toJson(gameService.listGames());
    }

    public void handleJoin(String username, String requestJson) throws DataAccessException {
        JoinGameRequest request = serializer.fromJson(requestJson, JoinGameRequest.class).addUsername(username);
        gameService.joinGame(request);
    }

    public void handleClear() throws DataAccessException {
        gameService.clear();
    }
}
