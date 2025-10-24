package chess;

import java.util.HashSet;

public class RookMovementRule extends BaseMovementRule{

    @Override
    public HashSet<ChessMove> moves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        calculateMoves(board, position, moves, 1, 0, true);
        calculateMoves(board, position, moves, -1, 0, true);
        calculateMoves(board, position, moves, 0, 1, true);
        calculateMoves(board, position, moves, 0, -1, true);

        return moves;
    }
}
