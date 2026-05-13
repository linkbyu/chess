package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalc implements PieceMovesCalc{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> results = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        upAndDownL(board, myPosition, row, col,2, results);
        upAndDownL(board, myPosition, row, col, -2, results);
        leftAndRightL(board, myPosition, row, col, 2, results);
        leftAndRightL(board, myPosition, row, col, -2, results);

        return results;


    }


    private void upAndDownL(ChessBoard board, ChessPosition myPosition,
                            int row, int col,
                            int movementY,
                            Collection<ChessMove> results) {
        row += movementY;

        if ( row < 9 && row > 0 ){
            singleSquareMove(board, myPosition, 1, 0, row, col, results);
            singleSquareMove(board, myPosition, -1, 0, row, col, results);
        }

    }

    static void singleSquareMove(ChessBoard board, ChessPosition myPosition,
                                  int movementX, int movementY,
                                  int row, int col,
                                  Collection<ChessMove> results) {
        row += movementY;
        col += movementX;

        if ( (row < 9 && row > 0) && (col < 9 && col > 0) ){
            var pendingSquare = new ChessPosition(row, col);
            var pendingPiece = board.getPiece(pendingSquare);

            if (pendingPiece == null ){ // empty square, so we can move there
                results.add(new ChessMove(myPosition, pendingSquare, null));
            }
            else if (pendingPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor() ){ // enemy piece is there, so add it to the move list
                results.add(new ChessMove(myPosition, pendingSquare, null));
            }
            // Otherwise, a piece of the same team is there, so it's not a valid move and we don't add anything
        }
    }

    private void leftAndRightL(ChessBoard board, ChessPosition myPosition,
                               int row, int col,
                               int movementX,
                               Collection<ChessMove> results) {
        col += movementX;

        if ( col < 9 && col > 0 ){
            singleSquareMove(board, myPosition, 0, 1, row, col, results);
            singleSquareMove(board, myPosition, 0, -1, row, col, results);
        }
    }
}
