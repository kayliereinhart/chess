package handler;

import com.google.gson.Gson;
import gamerequest.CreateGameRequest;
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
}
