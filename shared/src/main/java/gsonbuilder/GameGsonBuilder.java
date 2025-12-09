package gsonbuilder;

import chess.*;
import com.google.gson.*;

public class GameGsonBuilder {

    public Gson createSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(MovementRule.class, (JsonSerializer<MovementRule>)
                (rule, type, ctx) -> {
            JsonElement json = ctx.serialize(rule);
            JsonObject object = json.getAsJsonObject();

            if (rule instanceof PawnMovementRule) {
                object.addProperty("type", "PawnMovementRule");
            } else if (rule instanceof RookMovementRule) {
                object.addProperty("type", "RookMovementRule");
            } else if (rule instanceof BishopMovementRule) {
                object.addProperty("type", "BishopMovementRule");
            } else if (rule instanceof KnightMovementRule) {
                object.addProperty("type", "KnightMovementRule");
            } else if (rule instanceof QueenMovementRule) {
                object.addProperty("type", "QueenMovementRule");
            } else if (rule instanceof KingMovementRule) {
                object.addProperty("type", "KingMovementRule");
            }
            return object;
        });

        gsonBuilder.registerTypeAdapter(MovementRule.class,
                (JsonDeserializer<MovementRule>) (el, type, ctx) -> {
            if (!el.isJsonObject()) {
                return null;
            }
            String ruleType = el.getAsJsonObject().get("type").getAsString();
            return switch (ruleType) {
                case "PawnMovementRule" -> ctx.deserialize(el, PawnMovementRule.class);
                case "RookMovementRule" -> ctx.deserialize(el, RookMovementRule.class);
                case "KnightMovementRule" -> ctx.deserialize(el, KnightMovementRule.class);
                case "BishopMovementRule" -> ctx.deserialize(el, BishopMovementRule.class);
                case "QueenMovementRule" -> ctx.deserialize(el, QueenMovementRule.class);
                case "KingMovementRule" -> ctx.deserialize(el, KingMovementRule.class);
                default -> throw new JsonParseException("Unknown Movement Rule Type");
            };
        });

        return gsonBuilder.create();
    }
}
