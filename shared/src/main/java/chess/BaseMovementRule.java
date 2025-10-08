package chess;

import java.util.Collection;
import java.util.HashSet;

public abstract class BaseMovementRule implements MovementRule{

    public boolean inBounds(int row, int col) {
        return (row >= 1 && row <= 8 && col >= 1 && col <= 8);
    }

    public void calculateMoves(ChessBoard board, ChessPosition position,
                                                int rowAdd, int colAdd, boolean slide, Collection<ChessMove> moves) {
        int row = position.getRow() + rowAdd;
        int col = position.getColumn() + colAdd;
        ChessPosition endPosition;
        ChessPiece piece;

        while (inBounds(row, col)) {
            endPosition = new ChessPosition(row, col);
            piece = board.getPiece(endPosition);

            if (piece == null || piece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                moves.add(new ChessMove(position, endPosition, null));
            }

            if (slide && piece == null) {
                row += rowAdd;
                col += colAdd;
            } else {
                break;
            }
        }
    }

    public abstract HashSet<ChessMove> moves(ChessBoard board, ChessPosition position);
}
