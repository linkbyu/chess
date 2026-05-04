package chess;

import java.io.OutputStream;
import java.util.Collection;
import java.util.ArrayList;

public class BishopMovesCalculator implements PieceMovesCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final Collection<ChessMove> results = new ArrayList<>();

        diagonalMovements(board, myPosition, 1, 1, results);
        diagonalMovements(board, myPosition, -1, 1, results);
        diagonalMovements(board, myPosition, -1, -1, results);
        diagonalMovements(board, myPosition, 1, -1, results);

        return results;
    }


    private void diagonalMovements(ChessBoard board, ChessPosition myPosition,
                                                    int movementXIterator, int movementYIterator,
                                                    Collection<ChessMove> results){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        row += movementXIterator;
        col += movementYIterator;

        while( (row > 0 && row < 9) && (col > 0 && col < 9) ){
            var pendingSquare = new ChessPosition(row, col);

            if (board.getPiece(pendingSquare) == null){ // if there's no piece there
                results.add(new ChessMove(myPosition, pendingSquare, null));
            }
            else if (board.getPiece(myPosition).getTeamColor() == board.getPiece(pendingSquare).getTeamColor()){
                /* The two pieces have the same team color, so they can't capture one another */
                break;
            }
            else{ /* Otherwise, an Opponent's Piece is at the pendingSquare so we add it as a possible Move and then
                exit the loop */
                results.add(new ChessMove(myPosition, pendingSquare, null));
                break;
            }
            row += movementXIterator;
            col += movementYIterator;
        }
    }
}

