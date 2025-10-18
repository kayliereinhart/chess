package handler;

import com.google.gson.Gson;
import gamerequest.CreateGameRequest;
import io.javalin.http.HttpResponseException;
import model.GameData;
import service.GameService;

public class GameHandler {

    private final GameService gameService = new GameService();
    private final Gson serializer = new Gson();

    public String handleCreate(String authToken, String requestJson) throws HttpResponseException {
        String gameName = serializer.fromJson(requestJson, CreateGameRequest.class).gameName();
        CreateGameRequest request = new CreateGameRequest(authToken, gameName);
        GameData result = gameService.createGame(request);

        return serializer.toJson(result);
    }
}
