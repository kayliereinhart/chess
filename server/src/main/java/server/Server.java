package server;

import com.google.gson.Gson;
import datamodel.User;
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
        Gson serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, User.class);

        var res = userService.register(req);
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
