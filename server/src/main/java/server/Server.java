package server;

import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;

import handler.ExceptionHandler;
import handler.GameHandler;
import handler.UserHandler;
import server.websocket.ConnectionManager;
import server.websocket.WsHandler;

public class Server {

    private final Javalin server;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    private final ExceptionHandler exceptionHandler;
    private final WsHandler wsHandler;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        exceptionHandler = new ExceptionHandler();
        wsHandler = new WsHandler();

        try {
            userHandler = new UserHandler();
            gameHandler = new GameHandler();
        } catch (DataAccessException e) {
            throw new RuntimeException(String.format("Could not initialize database: %s", e.getMessage()));
        }

        // Clear endpoint
        server.delete("db", this::clear);

        // User endpoints
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);

        // Game endpoints
        server.post("game", this::createGame);
        server.get("game", this::listGames);
        server.put("game", this::joinGame);

        // Exceptions
        server.exception(Exception.class, this::handleException);
        server.exception(HttpResponseException.class, this::handleException);

        // WebSocket
        server.ws("/ws", ws -> {
            ws.onConnect(wsHandler);
            ws.onMessage(wsHandler);
            ws.onClose(wsHandler);
        });
    }

    private void clear(Context ctx) throws DataAccessException {
        userHandler.handleClear();
        gameHandler.handleClear();
    }

    private void register(Context ctx) throws DataAccessException {
        String requestJson = ctx.body();
        String responseJson = userHandler.handleRegister(requestJson);
        ctx.result(responseJson);
    }

    private void login(Context ctx) throws DataAccessException {
        String requestJson = ctx.body();
        String responseJson = userHandler.handleLogin(requestJson);
        ctx.result(responseJson);
    }

    private void logout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        userHandler.handleLogout(authToken);
    }

    private void createGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        userHandler.handleVerifyAuth(authToken);

        String requestJson = ctx.body();
        String responseJson = gameHandler.handleCreate(requestJson);
        ctx.result(responseJson);
    }

    private void listGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        userHandler.handleVerifyAuth(authToken);

        String responseJson = gameHandler.handleList();
        ctx.result(responseJson);
    }

    private void joinGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        String username = userHandler.handleVerifyAuth(authToken);

        String requestJson = ctx.body();
        gameHandler.handleJoin(username, requestJson);
    }

    private void handleException(Exception e, Context ctx) {
        exceptionHandler.handle(e, ctx);
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
