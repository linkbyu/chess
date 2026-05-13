package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable{

    private final ChessGame.TeamColor pieceColor;
    private PieceType type;
    private boolean hasNotMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        hasNotMoved = true;
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

    public void setPieceType(PieceType type) {
        this.type = type;
    }

    public boolean getHasNotMoved() {
        return hasNotMoved;
    }

    public void hasMoved() {
        hasNotMoved = false;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch(type){
            case KING:
                var kingCalc = new KingMovesCalc();
                yield kingCalc.pieceMoves(board, myPosition);

            case QUEEN:
                var queenCalc = new QueenMovesCalc();
                yield queenCalc.pieceMoves(board, myPosition);

            case BISHOP:
                var bishopCalc = new BishopMovesCalc();
                yield bishopCalc.pieceMoves(board, myPosition);

            case KNIGHT:
                var knightCalc = new KnightMovesCalc();
                yield knightCalc.pieceMoves(board, myPosition);

            case ROOK:
                var rookCalc = new RookMovesCalc();
                yield rookCalc.pieceMoves(board, myPosition);

            case PAWN:
                var pawnCalc = new PawnMovesCalc();
                yield pawnCalc.pieceMoves(board, myPosition);
        };
    }

    @Override
    public String toString() {
        return switch(pieceColor){
            case WHITE -> switch (type){
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

    @Override
    public ChessPiece clone(){
        try{
            ChessPiece clonedPiece = (ChessPiece) super.clone();
            clonedPiece.type = this.type;
            clonedPiece.hasNotMoved = this.hasNotMoved;

            return clonedPiece;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
