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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (piece.getPieceType() == PieceType.BISHOP) {
            while (row < 8 && col < 8) {
                row += 1;
                col += 1;
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }

            row = myPosition.getRow();
            col = myPosition.getColumn();

            while (row > 1 && col > 1) {
                row -= 1;
                col -= 1;
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }

            row = myPosition.getRow();
            col = myPosition.getColumn();

            while (row > 1 && col < 8) {
                row -= 1;
                col += 1;
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }

            row = myPosition.getRow();
            col = myPosition.getColumn();

            while (row < 8 && col > 1) {
                row += 1;
                col -= 1;
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }
        return moves;
    }
}
