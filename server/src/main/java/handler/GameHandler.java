package handler;

import com.google.gson.Gson;
import gamerequest.CreateGameRequest;
import gamerequest.JoinGameRequest;
import io.javalin.http.HttpResponseException;
import service.GameService;
import java.util.Map;

public class GameHandler {

    private final GameService gameService = new GameService();
    private final Gson serializer = new Gson();

    public String handleCreate(String authToken, String requestJson) throws HttpResponseException {
        String gameName = serializer.fromJson(requestJson, CreateGameRequest.class).gameName();
        CreateGameRequest request = new CreateGameRequest(authToken, gameName);
        Integer result = gameService.createGame(request);

        return serializer.toJson(Map.of("gameID", result));
    }

    public String handleList() {
        return serializer.toJson(gameService.listGames());
    }

    public void handleJoin(String username, String requestJson) {
        JoinGameRequest request = serializer.fromJson(requestJson, JoinGameRequest.class);
        request = new JoinGameRequest(username, request.playerColor(), request.gameID());
        gameService.joinGame(request);
    }

    public void handleClear() {
        gameService.clear();
    }
}
