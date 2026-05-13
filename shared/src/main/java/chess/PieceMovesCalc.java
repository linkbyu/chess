package chess;

import java.util.Collection;
import java.util.List;

public interface PieceMovesCalc {

    public default Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }
}
