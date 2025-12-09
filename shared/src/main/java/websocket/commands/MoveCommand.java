package websocket.commands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand {

    private final ChessMove move;

    public MoveCommand(CommandType type, String authToken, Integer id, ChessMove move) {
        super(type, authToken, id);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}
