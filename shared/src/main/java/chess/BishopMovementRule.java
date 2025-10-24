package chess;

import java.util.HashSet;

public class BishopMovementRule extends BaseMovementRule{

    @Override
    public HashSet<ChessMove> moves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();

        calculateMoves(board, position, moves, 1, 1, true);
        calculateMoves(board, position, moves, -1, 1, true);
        calculateMoves(board, position, moves, 1, -1, true);
        calculateMoves(board, position, moves, -1, -1, true);

        return moves;
    }
}
