package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {

    private final ChessGame.TeamColor color;

    public ConnectCommand(CommandType type, String authToken, Integer id, ChessGame.TeamColor color) {
        super(type, authToken, id);
        this.color = color;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
