package chess;

import java.util.HashSet;

public class KnightMovementRule extends BaseMovementRule {

    @Override
    public HashSet<ChessMove> moves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();

        calculateMoves(board, position, moves, 2, 1, false);
        calculateMoves(board, position, moves, 2, -1, false);
        calculateMoves(board, position, moves, -2, 1, false);
        calculateMoves(board, position, moves, -2, -1, false);
        calculateMoves(board, position, moves, 1, 2, false);
        calculateMoves(board, position, moves, 1, -2, false);
        calculateMoves(board, position, moves, -1, 2, false);
        calculateMoves(board, position, moves, -1, -2, false);

        return moves;
    }
}
