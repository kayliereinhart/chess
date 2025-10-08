package chess;

import java.util.HashSet;

public class KnightMovementRule extends BaseMovementRule {

    @Override
    public HashSet<ChessMove> moves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        calculateMoves(board, position, 2, 1, false, moves);
        calculateMoves(board, position, 2, -1, false, moves);
        calculateMoves(board, position, -2, 1, false, moves);
        calculateMoves(board, position, -2, -1, false, moves);
        calculateMoves(board, position, 1, 2, false, moves);
        calculateMoves(board, position, 1, -2, false, moves);
        calculateMoves(board, position, -1, 2, false, moves);
        calculateMoves(board, position, -1, -2, false, moves);

        return moves;
    }
}
