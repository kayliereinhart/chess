package handler;

import service.GameService;
import gamerequest.CreateGameRequest;
import gamerequest.JoinGameRequest;
import com.google.gson.Gson;
import java.util.Map;

public class GameHandler {

    private final GameService gameService = new GameService();
    private final Gson serializer = new Gson();

    public String handleCreate(String requestJson) {
        CreateGameRequest request = serializer.fromJson(requestJson, CreateGameRequest.class);
        Integer gameID = gameService.createGame(request);

        return serializer.toJson(Map.of("gameID", gameID));
    }

    public String handleList() {
        return serializer.toJson(gameService.listGames());
    }

    public void handleJoin(String username, String requestJson) {
        JoinGameRequest request = serializer.fromJson(requestJson, JoinGameRequest.class).addUsername(username);
        gameService.joinGame(request);
    }

    public void handleClear() {
        gameService.clear();
    }
}
