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
    private GameStatus status;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        status = GameStatus.INPLAY;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void changeStatus(GameStatus newStatus) {
        status = newStatus;
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

    public enum GameStatus {
        INPLAY,
        OVER
    }

    private void validateMove(Collection<ChessMove> valid, ChessMove move, ChessPiece piece) {
        ChessBoard testBoard = board.clone();
        testBoard.movePiece(move);

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
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        ChessPiece piece = getMovePiece(move);

        if (validMoves != null && validMoves.contains(move) && piece.getTeamColor() == teamTurn) {
            board.movePiece(move);
            changeTurn();
        } else if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your turn");
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

    private boolean canCaptureKing(ChessBoard chessBoard, ChessPiece piece, ChessPosition position,
                                   ChessPosition kingPosition, TeamColor kingTeam) {
        if (piece != null && piece.getTeamColor() != kingTeam) {
            Collection<ChessMove> moves = piece.pieceMoves(chessBoard, position);
            Collection<ChessPosition> endPositions = getEndPositions(moves);

            return endPositions.contains(kingPosition);
        }
        return false;
    }

    private boolean boardInCheck(TeamColor team, ChessBoard chessBoard) {
        ChessPosition kingPosition = chessBoard.findKing(team);

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = chessBoard.getPiece(position);

                if (canCaptureKing(chessBoard, piece, position, kingPosition, team)) {
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

    private boolean moveEscapesCheck(ChessMove move, TeamColor team) {
        ChessBoard testBoard = board.clone();
        testBoard.movePiece(move);

        return !boardInCheck(team, testBoard);
    }

    private boolean pieceEscapesCheck(ChessPosition position, TeamColor team) {
        Collection<ChessMove> moves = validMoves(position);

        if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() == team && moves != null) {
            for (ChessMove move : moves) {
                if (moveEscapesCheck(move, team)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition position = new ChessPosition(i, j);

                    if (pieceEscapesCheck(position, teamColor)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean hasMoves(ChessPosition position) {
        Collection<ChessMove> moves = validMoves(position);

        return (moves != null && !moves.isEmpty());
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);

                if (hasMoves(position) && board.getPiece(position).getTeamColor() == teamColor) {
                    return false;
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
