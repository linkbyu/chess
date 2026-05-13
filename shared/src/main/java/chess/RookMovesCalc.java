package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.BishopMovesCalc.slidingMovement;

public class RookMovesCalc implements PieceMovesCalc{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> results = new ArrayList<>();

        slidingMovement(board, myPosition, 0, 1, results);
        slidingMovement(board, myPosition, 0,-1, results);
        slidingMovement(board, myPosition, 1, 0, results);
        slidingMovement(board, myPosition, -1, 0, results);

        return results;
    }

}
