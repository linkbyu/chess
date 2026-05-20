package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        setUpBackRow(ChessGame.TeamColor.WHITE);
        setUpBackRow(ChessGame.TeamColor.BLACK);
        setUpPawnRow(ChessGame.TeamColor.WHITE);
        setUpPawnRow(ChessGame.TeamColor.BLACK);

    }

    private void setUpBackRow(ChessGame.TeamColor teamColor) {
        int backRow = switch(teamColor){
            case WHITE -> 1;
            case BLACK -> 8;
        };

        addPiece(new ChessPosition(backRow, 1), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(backRow, 2), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(backRow, 3), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(backRow, 4), new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN));

        addPiece(new ChessPosition(backRow, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(backRow, 6), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(backRow, 7), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(backRow, 8), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));

    }

    private void setUpPawnRow(ChessGame.TeamColor teamColor) {
        int pawnRow = switch(teamColor){
            case WHITE -> 2;
            case BLACK -> 7;
        };

        for (int col = 1; col < 9; col++){
            addPiece(new ChessPosition(pawnRow, col), new ChessPiece(teamColor, ChessPiece.PieceType.PAWN));
        }
    }


    @Override
    public String toString() {
        var builder = new StringBuilder();

        for (ChessPiece[] row : squares){
            builder.append("|");
            for (ChessPiece piece : row){
                if (piece != null){
                    builder.append(piece);
                    builder.append("|");
                }
                else{
                    builder.append(" ");
                    builder.append("|");
                }
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public Object clone() {
        try{
            ChessBoard clonedBoard = (ChessBoard) super.clone();

            ChessPiece[][] clonedSquares = new ChessPiece[8][8];
            for (int rowNum = 1; rowNum < 9; rowNum++){
                for (int colNum = 1; colNum < 9; colNum++){
                    ChessPiece piece = squares[rowNum - 1][colNum - 1];

                    if (piece != null) {
                        clonedSquares[rowNum - 1][colNum - 1] = piece.clone();
                    }
                }
            }
            clonedBoard.squares = clonedSquares;

            return clonedBoard;

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
