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

    private boolean outBounds(int row, int col) {
        return (row < 1 || row > 8 || col < 1 || col > 8);
    }

    private boolean captureEnemy(ChessBoard board, ChessPosition position) {
        return board.getPiece(position) != null && board.getPiece(position).getTeamColor() != pieceColor;
    }

    private boolean emptyOrCapture(ChessBoard board, ChessPosition position) {
        boolean positionEmpty = false;
        boolean capture = false;

        if (board.getPiece(position) == null) {
            positionEmpty = true;
        } else if (board.getPiece(position).getTeamColor() != pieceColor) {
            capture = true;
        }
        return positionEmpty || capture;
    }

    private void addMoves(ChessBoard board, ChessPosition startPosition, int rowAdd, int colAdd, List<ChessMove> moves) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        while (true) {
            row += rowAdd;
            col += colAdd;

            if (outBounds(row, col)) {
                break;
            }
            ChessPosition endPosition = new ChessPosition(row, col);

            if (emptyOrCapture(board, endPosition)) {
                moves.add(new ChessMove(startPosition, endPosition, null));
            }

            if (board.getPiece(endPosition) != null) {
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

    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                ChessPosition endPosition = new ChessPosition(row + i, col + j);

                if ((i != 0 || i != j) && !outBounds(row + i, col + j) && emptyOrCapture(board, endPosition)) {
                    moves.add(new ChessMove(myPosition, endPosition, null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // row - 2, col + 1
        // row - 2, col - 1
        // row + 2, col + 1
        // row + 2, col - 1
        // col - 2, row + 1
        // col - 2, row - 1
        // col + 2, row + 1
        // col + 2, row - 1

        for (int i = -2; i <= 2; i += 4) {
            for (int j = -1; j <= 1; j += 2) {
                ChessPosition endPosition1 = new ChessPosition(row + i, col + j);
                ChessPosition endPosition2 = new ChessPosition(row + j, col + i);

                if (!outBounds(row + i, col + j) && emptyOrCapture(board, endPosition1)) {
                    moves.add(new ChessMove(myPosition, endPosition1, null));
                }
                if (!outBounds(row + j, col + i) && emptyOrCapture(board, endPosition2)) {
                    moves.add(new ChessMove(myPosition, endPosition2, null));
                }
            }
        }
        return moves;
    }

    private void addPawnMoves(ChessPosition start, ChessPosition end, boolean promote, List<ChessMove> moves) {
        if (promote) {
            moves.add(new ChessMove(start, end, PieceType.KNIGHT));
            moves.add(new ChessMove(start, end, PieceType.ROOK));
            moves.add(new ChessMove(start, end, PieceType.BISHOP));
            moves.add(new ChessMove(start, end, PieceType.QUEEN));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int direction = 1;
        boolean firstMove = false;
        boolean promote = false;

        if (pieceColor == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }

        if ((pieceColor == ChessGame.TeamColor.WHITE && row == 2) || (pieceColor == ChessGame.TeamColor.BLACK && row == 7)) {
            firstMove = true;
        } else if ((pieceColor == ChessGame.TeamColor.WHITE && row == 7) || (pieceColor == ChessGame.TeamColor.BLACK && row == 2)) {
            promote = true;
        }
        ChessPosition endPosition = new ChessPosition(row + direction, col);

        if (!outBounds(row + direction, col) && board.getPiece(endPosition) == null) {
            addPawnMoves(myPosition, endPosition, promote, moves);
        }
        endPosition = new ChessPosition(row + (2 * direction), col);

        if (firstMove && !outBounds(row + (2 * direction), col) && board.getPiece(endPosition) == null && board.getPiece(new ChessPosition(row + direction, col)) == null) {
            moves.add(new ChessMove(myPosition, endPosition, null));
        }
        endPosition = new ChessPosition(row + direction, col - 1);

        if (!outBounds(row + direction, col - 1) && captureEnemy(board, endPosition)) {
            addPawnMoves(myPosition, endPosition, promote, moves);
        }
        endPosition = new ChessPosition(row + direction, col + 1);

        if (!outBounds(row + direction, col + 1) && captureEnemy(board, endPosition)) {
            addPawnMoves(myPosition, endPosition, promote, moves);
        }

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
        } else if (piece.getPieceType() == PieceType.QUEEN) {
            Collection<ChessMove> queenMoves = getBishopMoves(board, myPosition);
            queenMoves.addAll(getRookMoves(board, myPosition));

            return queenMoves;
        } else if (piece.getPieceType() == PieceType.KING) {
            return getKingMoves(board, myPosition);
        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            return getKnightMoves(board, myPosition);
        } else if (piece.getPieceType() == PieceType.PAWN) {
            return getPawnMoves(board, myPosition);
        } else {
            return List.of();
        }
    }
}
