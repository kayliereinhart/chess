package server;

import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.post("user", this::register);

    }

    private void register(Context ctx) {
        //send json to RegisterHandler
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
