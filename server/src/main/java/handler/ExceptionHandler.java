package handler;

import com.google.gson.Gson;
import service.AlreadyTakenException;
import io.javalin.http.Context;
import java.util.Map;

public class ExceptionHandler {

    public void handle(Exception e, Context ctx) {
        String body = new Gson().toJson(Map.of("message",
                String.format("Error: %s", e.getMessage()), "success", false));

        if (e.getClass() == AlreadyTakenException.class) {
            ctx.status(403);
        } else {
            ctx.status(500);
        }
        ctx.json(body);
    }
}
