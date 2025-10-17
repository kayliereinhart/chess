package handler;

import com.google.gson.Gson;
import io.javalin.http.HttpResponseException;
import io.javalin.http.Context;

import java.util.Map;

public class ExceptionHandler {

    public void handle(Exception e, Context ctx) {
        String body = new Gson().toJson(Map.of("message",
                String.format("Error: %s", e.getMessage()), "success", false));
        ctx.status(((HttpResponseException) e).getStatus());
        ctx.json(body);
    }
}
