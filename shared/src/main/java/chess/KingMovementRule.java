package chess;

import java.util.HashSet;

public class KingMovementRule extends BaseMovementRule {

    @Override
    public HashSet<ChessMove> moves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        calculateMoves(board, position, moves, 1, 1, false);
        calculateMoves(board, position, moves, -1, 1, false);
        calculateMoves(board, position, moves, 1, -1, false);
        calculateMoves(board, position, moves, -1, -1, false);
        calculateMoves(board, position, moves, 1, 0, false);
        calculateMoves(board, position, moves, -1, 0, false);
        calculateMoves(board, position, moves, 0, 1, false);
        calculateMoves(board, position, moves, 0, -1, false);

        return moves;
    }
}
