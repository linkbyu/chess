package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalc implements PieceMovesCalc{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> results = new ArrayList<>();

        slidingMovement(board, myPosition, 1, 1, results);
        slidingMovement(board, myPosition, -1, 1, results);
        slidingMovement(board, myPosition, -1, -1, results);
        slidingMovement(board, myPosition, 1, -1, results);

        return results;
    }

    static void slidingMovement(ChessBoard board, ChessPosition myPosition,
                                 int movementXIter, int movementYIter,
                                 Collection<ChessMove> results) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        row += movementYIter;
        col += movementXIter;

        while ( (row < 9 && row > 0 ) && (col < 9 && col > 0 ) ){
            var pendingSquare = new ChessPosition(row, col);
            var pendingPiece = board.getPiece(pendingSquare);

            if ( pendingPiece == null ){ // no piece there
                results.add(new ChessMove(myPosition, pendingSquare, null));
            }
            else if (pendingPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor() ){ // enemy piece is there, so add it to the move list
                results.add(new ChessMove(myPosition, pendingSquare, null));
                break;
            }
            else{ // Otherwise, the square contains a piece of the same team so we break the loop
                break;
            }

            row += movementYIter;
            col += movementXIter;
        }

    }

}
