package server;

import dataaccess.DataAccessException;
import handler.ExceptionHandler;
import handler.RegisterHandler;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin server;
    private final RegisterHandler registerHandler;
    private final ExceptionHandler exceptionHandler;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        registerHandler = new RegisterHandler();
        exceptionHandler = new ExceptionHandler();

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);

        server.exception(Exception.class, this::handleException);

    }

    private void register(Context ctx) throws Exception {
        String requestJson = ctx.body();
        String responseJson = registerHandler.handleRegister(requestJson);
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
