package server;

import handler.ExceptionHandler;
import handler.UserHandler;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;

public class Server {

    private final Javalin server;
    private final UserHandler userHandler;
    private final ExceptionHandler exceptionHandler;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        userHandler = new UserHandler();
        exceptionHandler = new ExceptionHandler();

        // Register your endpoints and exception handlers here.
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);

        server.exception(HttpResponseException.class, this::handleException);

    }

    private void register(Context ctx) throws HttpResponseException {
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
        String requestJson = ctx.body();
        userHandler.handleLogout(requestJson);
    }

    private void clear(Context ctx) {
        userHandler.handleClear();
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
