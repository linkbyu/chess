package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.KnightMovesCalculator.singleSquareMove;

public class KingMovesCalculator implements PieceMovesCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final Collection<ChessMove> results = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        singleSquareMove(board, myPosition, 1, 0, row, col, results);
        singleSquareMove(board, myPosition, 1, 1, row, col, results);
        singleSquareMove(board, myPosition, 0, 1, row, col, results);
        singleSquareMove(board, myPosition, -1, 1, row, col, results);

        singleSquareMove(board, myPosition, -1, 0, row, col, results);
        singleSquareMove(board, myPosition, -1, -1, row, col, results);
        singleSquareMove(board, myPosition, 0, -1, row, col, results);
        singleSquareMove(board, myPosition, 1, -1, row, col, results);

        return results;
    }


}
