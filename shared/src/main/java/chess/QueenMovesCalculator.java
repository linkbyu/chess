package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.BishopMovesCalculator.slideMovements;

public class QueenMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final Collection<ChessMove> results = new ArrayList<>();

        slideMovements(board, myPosition, 1, 0, results);
        slideMovements(board, myPosition, 1, 1, results);
        slideMovements(board, myPosition, 0, 1, results);
        slideMovements(board, myPosition, -1, 1, results);
        slideMovements(board, myPosition, -1, 0, results);
        slideMovements(board, myPosition, -1, -1, results);
        slideMovements(board, myPosition, 0, -1, results);
        slideMovements(board, myPosition, 1, -1, results);


        return results;
    }
}
