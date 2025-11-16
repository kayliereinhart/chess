package chess;

import java.util.Objects;
import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    public void movePiece(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = getPiece(startPosition);

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        board[endPosition.getRow() - 1][endPosition.getColumn() - 1] = piece;
        board[startPosition.getRow() - 1][startPosition.getColumn() - 1] = null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i <= 7; i++) {
            if (i == 0 || i == 7) {
                board[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
            } else if (i == 1 || i == 6) {
                board[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
            } else if (i == 2 || i == 5) {
                board[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
            } else if (i == 3) {
                board[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
            } else {
                board[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
            }
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }

    private int switchNumbers(int num) {
        return switch (num) {
            case 0 -> 8;
            case 1 -> 7;
            case 2 -> 6;
            case 3 -> 5;
            case 4 -> 4;
            case 5 -> 3;
            case 6 -> 2;
            case 7 -> 1;
            default -> 0;
        };
    }

    public ChessBoard flipBoard() {
        ChessBoard flipped = new ChessBoard();

        for (int i = 0; i < 8; i++) {
            int row = switchNumbers(i);

            for (int j = 0; j < 8; j++) {
                int col = switchNumbers(j);
                ChessPiece piece = getPiece(new ChessPosition(i + 1, j + 1));
                flipped.addPiece(new ChessPosition(row, col), piece);
            }
        }
        return flipped;
    }

    private boolean isKingPosition(ChessPosition position, ChessGame.TeamColor team) {
        ChessPiece piece = getPiece(position);

        return (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == team);
    }

    public ChessPosition findKing(ChessGame.TeamColor team) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);

                if (isKingPosition(position, team)) {
                    return position;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                if (board[i][j] == null) {
                    str.append("null");
                } else {
                    str.append(board[i][j].toString());
                }
            }
        }
        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();
            clone.board = new ChessPiece[8][8];

            for (int i = 0; i < 8; i++) {
                clone.board[i] = Arrays.copyOf(board[i], 8);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
