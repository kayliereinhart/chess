package chess;

import java.util.HashMap;

public class Rules {

    public HashMap<ChessPiece.PieceType, MovementRule> rules = new HashMap<>();

    public Rules() {
        rules.put(ChessPiece.PieceType.BISHOP, new BishopMovementRule());
        rules.put(ChessPiece.PieceType.ROOK, new RookMovementRule());
        rules.put(ChessPiece.PieceType.QUEEN, new QueenMovementRule());
        rules.put(ChessPiece.PieceType.KING, new KingMovementRule());
        rules.put(ChessPiece.PieceType.KNIGHT, new KnightMovementRule());
        rules.put(ChessPiece.PieceType.PAWN, new PawnMovementRule());
    }

    public MovementRule pieceMoves(ChessPiece.PieceType type) {
        return rules.get(type);
    }
}
