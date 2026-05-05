package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements  PieceMovesCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final Collection<ChessMove> results = new ArrayList<>();

        upAndDownL(board, myPosition, 2, results);
        upAndDownL(board, myPosition, -2, results);
        leftAndRightL(board, myPosition, 2, results);
        leftAndRightL(board, myPosition, -2, results);

        return results;
    }


    private void upAndDownL(ChessBoard board, ChessPosition myPosition,
                           int movementY, Collection<ChessMove> results){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        row += movementY;

        if ( row > 0 && row < 9 ) {
            singleLMove(board, myPosition, -1, 0, row, col, results);
            singleLMove(board, myPosition, 1, 0, row, col, results);
        }
    }


    private void singleLMove(ChessBoard board, ChessPosition myPosition,
                           int movementX, int movementY,
                           int row, int col,
                           Collection<ChessMove> results){
        row += movementY;
        col += movementX;

        if ( (row > 0 && row < 9) && (col > 0 && col < 9) ) {
            var pendingSquare = new ChessPosition(row, col);

            if (board.getPiece(pendingSquare) == null){ // if there's no piece there
                results.add(new ChessMove(myPosition, pendingSquare, null));
            }
            else if (board.getPiece(myPosition).getTeamColor() != board.getPiece(pendingSquare).getTeamColor()){
                /* an Opponent's Piece is at the pendingSquare so we add it as a possible Move */
                results.add(new ChessMove(myPosition, pendingSquare, null));
            }
            // Otherwise, the two pieces have the same team color, so they can't capture one another and we do nothing
        }

    }

    private void leftAndRightL(ChessBoard board, ChessPosition myPosition,
                               int movementX, Collection<ChessMove> results){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        col += movementX;

        if ( col > 0 && col < 9 ) {
            singleLMove(board, myPosition, 0, 1, row, col, results);
            singleLMove(board, myPosition, 0, -1, row, col, results);
        }
    }

}
