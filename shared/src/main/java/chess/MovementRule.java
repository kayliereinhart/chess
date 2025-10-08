package chess;

import java.util.HashSet;

public interface MovementRule {
    public HashSet<ChessMove> moves(ChessBoard board, ChessPosition position);
}
