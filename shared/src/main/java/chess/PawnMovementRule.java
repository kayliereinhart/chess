package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMovementRule extends BaseMovementRule {

    private void addPawnMoves(ChessPosition start, ChessPosition end, boolean promote, Collection<ChessMove> moves) {
        if (promote) {
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }

    private void addForwardMoves(ChessBoard board, ChessPosition position, int direction,
                                 boolean firstMove, boolean promote, Collection<ChessMove> moves) {
        int row = position.getRow() + direction;
        int col = position.getColumn();
        ChessPosition endPosition = new ChessPosition(row, col);

        if (inBounds(row, col) && board.getPiece(endPosition) == null) {
            addPawnMoves(position, endPosition, promote, moves);

            endPosition = new ChessPosition(row + direction, col);
            if (firstMove && board.getPiece(endPosition) == null) {
                addPawnMoves(position, endPosition, promote, moves);
            }
        }
    }

    private void addDiagonalMoves(ChessBoard board, ChessPosition position, int direction,
                                  boolean promote, Collection<ChessMove> moves) {
        int row = position.getRow() + direction;
        int col = position.getColumn();
        ChessPosition endPosition = new ChessPosition(row, col + 1);

        if (inBounds(row, col + 1) && board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != board.getPiece(position).getTeamColor()) {
            addPawnMoves(position, endPosition, promote, moves);
        }

        endPosition = new ChessPosition(row, col - 1);
        if (inBounds(row, col - 1) && board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != board.getPiece(position).getTeamColor()) {
            addPawnMoves(position, endPosition, promote, moves);
        }
    }

    public void calculatePawnMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int row = position.getRow();
        int col = position.getColumn();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        int direction = 1;
        boolean firstMove = false;
        boolean promote = false;

        if (color == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }
        if ((color == ChessGame.TeamColor.WHITE && row == 2) || (color == ChessGame.TeamColor.BLACK && row == 7)) {
            firstMove = true;
        } else if ((color == ChessGame.TeamColor.WHITE && row == 7) || (color == ChessGame.TeamColor.BLACK && row == 2)) {
            promote = true;
        }

        addForwardMoves(board, position, direction, firstMove, promote, moves);
        addDiagonalMoves(board, position, direction, promote, moves);
    }

    @Override
    public HashSet<ChessMove> moves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        calculatePawnMoves(board, position, moves);

        return moves;
    }
}
