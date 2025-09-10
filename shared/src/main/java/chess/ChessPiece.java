package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    private void addMoves(ChessBoard board, ChessPosition startPosition, int rowAdd, int colAdd, List<ChessMove>moves) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        boolean previousNull = false;

        while (true) {
            row += rowAdd;
            col += colAdd;

            if (row < 1 || row > 8 || col < 1 || col > 8) {
                break;
            }
            ChessPosition endPosition = new ChessPosition(row, col);

            if (board.getPiece(endPosition) == null || previousNull) {
                moves.add(new ChessMove(startPosition, endPosition, null));
            }

            if (board.getPiece(endPosition) == null) {
                previousNull = true;
            } else {
                break;
            }
        }
    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();

        addMoves(board, myPosition, 1, 1, moves);
        addMoves(board, myPosition, -1, -1, moves);
        addMoves(board, myPosition, 1, -1, moves);
        addMoves(board, myPosition, -1, 1, moves);

        return moves;
    }

    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();

        addMoves(board, myPosition, 1, 0, moves);
        addMoves(board, myPosition, -1, 0, moves);
        addMoves(board, myPosition, 0, 1, moves);
        addMoves(board, myPosition, 0, -1, moves);

        return moves;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getPieceType() == PieceType.BISHOP) {
            return getBishopMoves(board, myPosition);
        } else if (piece.getPieceType() == PieceType.ROOK) {
            return getRookMoves(board, myPosition);
        } else {
            return List.of();
        }
    }
}
