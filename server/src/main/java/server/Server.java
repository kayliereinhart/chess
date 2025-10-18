package server;

import handler.ExceptionHandler;
import handler.GameHandler;
import handler.UserHandler;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;

public class Server {

    private final Javalin server;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    private final ExceptionHandler exceptionHandler;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        userHandler = new UserHandler();
        exceptionHandler = new ExceptionHandler();
        gameHandler = new GameHandler();

        // Register your endpoints and exception handlers here.
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);

        server.post("game", this::createGame);
        server.get("game", this::listGames);

        server.exception(HttpResponseException.class, this::handleException);

    }

    private void clear(Context ctx) {
        userHandler.handleClear();
        gameHandler.handleClear();
    }

    private void register(Context ctx) throws HttpResponseException {
        String requestJson = ctx.body();
        String responseJson = userHandler.handleRegister(requestJson);
        ctx.result(responseJson);
    }

    private void login(Context ctx) throws HttpResponseException {
        String requestJson = ctx.body();
        String responseJson = userHandler.handleLogin(requestJson);
        ctx.result(responseJson);
    }

    private void logout(Context ctx) throws HttpResponseException {
        String authToken = ctx.header("authorization");
        userHandler.handleLogout(authToken);
    }

    private void createGame(Context ctx) throws HttpResponseException {
        //Question: If userService only uses authToken and gameService only uses gameName,
        // do I need CreateGameRequest to combine the two?
        //Question: Should gameService throw the UnauthorizedResponse when invalid authToken?
        // if so, how does gameService access the auths HashMap?
        String authToken = ctx.header("authorization");
        userHandler.handleVerifyAuth(authToken);

        String requestJson = ctx.body();
        String responseJson = gameHandler.handleCreate(authToken, requestJson);
        ctx.result(responseJson);
    }

    private void listGames(Context ctx) {
        String authToken = ctx.header("authorization");
        String responseJson = gameHandler.handleList(authToken);
        ctx.result(responseJson);
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
