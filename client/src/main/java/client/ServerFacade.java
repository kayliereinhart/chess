package client;

import com.google.gson.Gson;
import gsonbuilder.GameGsonBuilder;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private final Gson serializer;

    public ServerFacade(String url) {
        serverUrl = url;
        GameGsonBuilder builder = new GameGsonBuilder();
        serializer = builder.createSerializer();
    }

    public void clear() throws Exception {
        var request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }

    public AuthData register(UserData userData) throws Exception {
        var request = buildRequest("POST", "/user", userData, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(UserData userData) throws Exception {
        var request = buildRequest("POST", "/session", userData, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public CreateGameResult createGame(CreateGameRequest name, String authToken) throws Exception {
        var request = buildRequest("POST", "/game", name, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public void joinGame(JoinGameRequest joinRequest, String authToken) throws Exception {
        var request = buildRequest("PUT", "/game", joinRequest, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String header) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (header != null) {
            request.header("authorization", header);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(serializer.toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new Exception(serializer.fromJson(body, Response.class).message());
            }

            throw new Exception("other failure: " + status);
        }

        if (responseClass != null) {
            return serializer.fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
