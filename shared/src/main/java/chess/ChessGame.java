package chess;

import java.util.Objects;
import java.util.Collection;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private void validateMove(Collection<ChessMove> valid, ChessMove move, ChessPiece piece) {
        // Change this so that it calls board make move method
        ChessBoard testBoard = board.clone();
        testBoard.addPiece(move.getEndPosition(), testBoard.getPiece(move.getStartPosition()));
        testBoard.addPiece(move.getStartPosition(), null);

        if (!boardInCheck(piece.getTeamColor(), testBoard)) {
            valid.add(move);
        }
    }

    private void validateMoves(Collection<ChessMove> moves, Collection<ChessMove> valid, ChessPiece piece) {
        for (ChessMove move : moves) {
            validateMove(valid, move, piece);
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> valid = new ArrayList<>();

        validateMoves(moves, valid, piece);

        return valid;
    }

    private ChessPiece getMovePiece(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        return piece;
    }

    private void changeTurn() {
        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        ChessPiece piece = getMovePiece(move);
        // Change this so it calls a make move method from the ChessBoard class

        if (valid != null && valid.contains(move) && piece.getTeamColor() == teamTurn) {
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);

            changeTurn();
        } else {
            throw new InvalidMoveException("Invalid Move");
        }
    }

    private Collection<ChessPosition> getEndPositions(Collection<ChessMove> moves) {
        Collection<ChessPosition> endPositions = new ArrayList<>();

        for (ChessMove move : moves) {
            endPositions.add(move.getEndPosition());
        }
        return endPositions;
    }

    private boolean canCaptureKing(ChessBoard chessBoard, ChessPiece piece, ChessPosition kingPos, ChessPosition pos, TeamColor teamColor) {
        if (piece != null && piece.getTeamColor() != teamColor) {
            Collection<ChessMove> moves = piece.pieceMoves(chessBoard, pos);
            Collection<ChessPosition> endPositions = getEndPositions(moves);

            return endPositions.contains(kingPos);
        }
        return false;
    }

    private boolean boardInCheck(TeamColor teamColor, ChessBoard chessBoard) {
        ChessPosition kingPos = chessBoard.findKing(teamColor);

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = chessBoard.getPiece(pos);

                if (canCaptureKing(chessBoard, piece, kingPos, pos, teamColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return boardInCheck(teamColor, board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            // Change so this calls ChessBoard make move method

            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition pos = new ChessPosition(i, j);
                    Collection<ChessMove> moves = validMoves(pos);

                    if (board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() == teamColor && moves != null) {
                        for (ChessMove move : moves) {
                            ChessBoard testBoard = board.clone();
                            testBoard.addPiece(move.getEndPosition(), testBoard.getPiece(pos));
                            testBoard.addPiece(pos, null);

                            if (!boardInCheck(teamColor, testBoard)) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition pos;
        Collection<ChessMove> moves = null;

        if (isInCheck(teamColor)) {
            return false;
        } else {
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    pos = new ChessPosition(i, j);

                    if (board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() == teamColor) {
                        moves = validMoves(pos);
                    }
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
