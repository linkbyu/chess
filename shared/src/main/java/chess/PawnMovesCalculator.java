package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final Collection<ChessMove> results = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessPiece piece = board.getPiece(myPosition);
        var teamColor = piece.getTeamColor();
        switch (teamColor) {
            case WHITE:
                if ( row + 1 < 9 ){
                    var pendingSquare = new ChessPosition(row + 1, col);

                    if (board.getPiece(pendingSquare) == null){ // if there's no piece in the 1st square ahead, add as an available move
                        results.add(new ChessMove(myPosition, pendingSquare, null));

                        if ( onJumpSpot(myPosition) && ( (row + 2*movementY < 9) && (row + 2*movementY > 0)) ){ // For moving 2 squares ahead on pawn's first move
                            var pendingSquare2 = new ChessPosition(row + 2*movementY, col);

                            if (board.getPiece(pendingSquare2) == null){ // if there's no piece in the 2nd square ahead, add it as an available move
                                results.add(new ChessMove(myPosition, pendingSquare2, null));
                            }
                        }
                    }

                    if (col + 1 < 9){ // RIGHT DIAGONAL CAPTURE
                        var pendingDiagonalSquare = new ChessPosition(row + movementY, col + 1);

                        if (board.getPiece(pendingDiagonalSquare) != null){ // capture
                            results.add(new ChessMove(myPosition, pendingDiagonalSquare, null));
                        }
                    }
                    if (col - 1 > 0){ // LEFT DIAGONAL CAPTURE
                        var pendingDiagonalSquare = new ChessPosition(row + movementY, col - 1);

                        if (board.getPiece(pendingDiagonalSquare) != null){ // capture
                            results.add(new ChessMove(myPosition, pendingDiagonalSquare, null));
                        }
                    }
                }
            case BLACK:

            default -> throw new IllegalArgumentException();
        };

        if ( (row + movementY < 9) && (row + movementY > 0) ){
            var pendingSquare = new ChessPosition(row + movementY, col);

            if (board.getPiece(pendingSquare) == null){ // if there's no piece in the 1st square ahead, add as an available move
                results.add(new ChessMove(myPosition, pendingSquare, null));

                if ( onJumpSpot(myPosition) && ( (row + 2*movementY < 9) && (row + 2*movementY > 0)) ){ // For moving 2 squares ahead on pawn's first move
                    var pendingSquare2 = new ChessPosition(row + 2*movementY, col);

                    if (board.getPiece(pendingSquare2) == null){ // if there's no piece in the 2nd square ahead, add it as an available move
                        results.add(new ChessMove(myPosition, pendingSquare2, null));
                    }
                }
            }

            if (col + 1 < 9){ // RIGHT DIAGONAL CAPTURE
                var pendingDiagonalSquare = new ChessPosition(row + movementY, col + 1);

                if (board.getPiece(pendingDiagonalSquare) != null){ // capture
                    results.add(new ChessMove(myPosition, pendingDiagonalSquare, null));
                }
            }
            if (col - 1 > 0){ // LEFT DIAGONAL CAPTURE
                var pendingDiagonalSquare = new ChessPosition(row + movementY, col - 1);

                if (board.getPiece(pendingDiagonalSquare) != null){ // capture
                    results.add(new ChessMove(myPosition, pendingDiagonalSquare, null));
                }
            }
        }

        return results;
    }

    private boolean onJumpSpot(ChessPosition myPosition){
        ArrayList<ChessPosition> whiteJumpSpots = {ChessPosition(2, 1)}

        ArrayList<ChessPosition> blackJumpSpots = {ChessPosition()};
        if ()
    }

}
