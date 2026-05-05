package chess;

import java.util.Collection;
import java.util.Objects;

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
        PieceType pieceType = piece.getPieceType();

        return switch(pieceType) {
            case PieceType.BISHOP:
                var bishopCalc = new BishopMovesCalculator();

                yield bishopCalc.pieceMoves(board, myPosition);

            case PieceType.PAWN:
                var pawnCalc = new PawnMovesCalculator();
                yield pawnCalc.pieceMoves(board, myPosition);

            case PieceType.ROOK:
                var rookCalc = new RookMovesCalculator();
                yield rookCalc.pieceMoves(board, myPosition);

            case PieceType.QUEEN:
                var queenCalc = new QueenMovesCalculator();
                yield queenCalc.pieceMoves(board, myPosition);

            case PieceType.KNIGHT:
                var knightCalc = new KnightMovesCalculator();
                yield knightCalc.pieceMoves(board, myPosition);

            case PieceType.KING:
                var kingCalc = new KingMovesCalculator();
                yield kingCalc.pieceMoves(board, myPosition);
        };
    }


    @Override
    public String toString() {

        return switch(pieceColor){
            case WHITE -> switch(type){
                case PAWN -> "P";
                case ROOK -> "R";
                case KNIGHT -> "N";
                case BISHOP -> "B";
                case QUEEN -> "Q";
                case KING -> "K";
            };
            case BLACK -> switch (type){
                case PAWN -> "p";
                case ROOK -> "r";
                case KNIGHT -> "n";
                case BISHOP -> "b";
                case QUEEN -> "q";
                case KING -> "k";
            };
        };
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

}
