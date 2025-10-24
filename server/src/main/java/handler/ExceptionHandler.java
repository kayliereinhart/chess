package handler;

import java.util.Map;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;

public class ExceptionHandler {

    public void handle(Exception e, Context ctx) {
        String body = new Gson().toJson(Map.of("message",
                String.format("Error: %s", e.getMessage()), "success", false));

        if (e instanceof HttpResponseException httpEx) {
            ctx.status(httpEx.getStatus());
        } else {
            ctx.status(500);
        }
        ctx.json(body);
    }
}
