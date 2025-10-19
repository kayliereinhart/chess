package server;

import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;

import handler.ExceptionHandler;
import handler.GameHandler;
import handler.UserHandler;

public class Server {

    private final Javalin server;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    private final ExceptionHandler exceptionHandler;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        userHandler = new UserHandler();
        gameHandler = new GameHandler();
        exceptionHandler = new ExceptionHandler();

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
        server.exception(HttpResponseException.class, this::handleException);
    }

    private void clear(Context ctx) {
        userHandler.handleClear();
        gameHandler.handleClear();
    }

    private void register(Context ctx) {
        String requestJson = ctx.body();
        String responseJson = userHandler.handleRegister(requestJson);
        ctx.result(responseJson);
    }

    private void login(Context ctx) {
        String requestJson = ctx.body();
        String responseJson = userHandler.handleLogin(requestJson);
        ctx.result(responseJson);
    }

    private void logout(Context ctx) {
        String authToken = ctx.header("authorization");
        userHandler.handleLogout(authToken);
    }

    private void createGame(Context ctx) {
        //Question: If userService only uses authToken and gameService only uses gameName,
        // do I need CreateGameRequest to combine the two?
        //Question: Should gameService throw the UnauthorizedResponse when invalid authToken?
        // if so, how does gameService access the auths HashMap?
        String authToken = ctx.header("authorization");
        userHandler.handleVerifyAuth(authToken);

        String requestJson = ctx.body();
        String responseJson = gameHandler.handleCreate(requestJson);
        ctx.result(responseJson);
    }

    private void listGames(Context ctx) {
        // Do I need ListGamesResult object? Can I use Map.of instead?
        String authToken = ctx.header("authorization");
        userHandler.handleVerifyAuth(authToken);

        String responseJson = gameHandler.handleList();
        ctx.result(responseJson);
    }

    private void joinGame(Context ctx) {
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
