package handler;

import com.google.gson.Gson;
import io.javalin.http.HttpResponseException;
import service.GameService;

public class GameHandler {

    private final GameService gameService = new GameService();
    private final Gson serializer = new Gson();

    public String handleCreate(String authToken, String requestJson) throws HttpResponseException {
        return "";
    }
}
