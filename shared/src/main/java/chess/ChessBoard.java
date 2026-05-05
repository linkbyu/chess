package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];

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
        backRow(ChessGame.TeamColor.WHITE);
        backRow(ChessGame.TeamColor.BLACK);
        pawnRow(ChessGame.TeamColor.WHITE);
        pawnRow(ChessGame.TeamColor.BLACK);
    }


    private void backRow(ChessGame.TeamColor teamColor){
        int row = switch (teamColor) {
            case ChessGame.TeamColor.WHITE -> 1;
            case ChessGame.TeamColor.BLACK -> 8;
        };

        addPiece(new ChessPosition(row, 1), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 2), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 3), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 4), new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row, 6), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 7), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 8), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));

    }


    private void pawnRow(ChessGame.TeamColor teamColor){
        int row = switch (teamColor) {
            case ChessGame.TeamColor.WHITE -> 2;
            case ChessGame.TeamColor.BLACK -> 7;
        };

        for (int col = 1; col < 9; col++){
            addPiece(new ChessPosition(row, col), new ChessPiece(teamColor, ChessPiece.PieceType.PAWN));
        }
    }


    @Override
    public String toString() {
        var boardOutput = new StringBuilder();
        for (ChessPiece[] row : squares){
            boardOutput.append("|");
            for (ChessPiece piece : row){
                if (piece != null) {
                    boardOutput.append(piece);
                }
                else boardOutput.append(" ");
                boardOutput.append("|");
            }
            boardOutput.append("\n");
        }

        return boardOutput.toString();
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
}

