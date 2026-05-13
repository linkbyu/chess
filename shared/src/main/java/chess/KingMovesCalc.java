package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.KnightMovesCalc.singleSquareMove;

public class KingMovesCalc implements PieceMovesCalc{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> results = new ArrayList<>();
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
