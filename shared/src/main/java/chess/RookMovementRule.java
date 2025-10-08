package chess;

import java.util.HashSet;

public class RookMovementRule extends BaseMovementRule{

    @Override
    public HashSet<ChessMove> moves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        calculateMoves(board, position, 1, 0, true, moves);
        calculateMoves(board, position, -1, 0, true, moves);
        calculateMoves(board, position, 0, 1, true, moves);
        calculateMoves(board, position, 0, -1, true, moves);

        return moves;
    }
}
