package chess;

import java.util.HashSet;

public class KingMovementRule extends BaseMovementRule {

    @Override
    public HashSet<ChessMove> moves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        calculateMoves(board, position, 1, 1, false, moves);
        calculateMoves(board, position, -1, 1, false, moves);
        calculateMoves(board, position, 1, -1, false, moves);
        calculateMoves(board, position, -1, -1, false, moves);
        calculateMoves(board, position, 1, 0, false, moves);
        calculateMoves(board, position, -1, 0, false, moves);
        calculateMoves(board, position, 0, 1, false, moves);
        calculateMoves(board, position, 0, -1, false, moves);

        return moves;
    }
}
