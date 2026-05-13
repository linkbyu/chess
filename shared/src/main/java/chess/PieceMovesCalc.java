package chess;

import java.util.Collection;

public interface PieceMovesCalc {

    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
