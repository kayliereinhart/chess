package server;

import handler.RegisterHandler;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin server;
    private final RegisterHandler registerHandler = new RegisterHandler();

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);

    }

    private void register(Context ctx) {
        //send json to RegisterHandler
        String reqJson = ctx.body();
        String res = registerHandler.handleRegister(reqJson);

        ctx.result(res);
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
