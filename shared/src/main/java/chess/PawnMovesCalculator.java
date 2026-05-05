package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final Collection<ChessMove> results = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessPiece piece = board.getPiece(myPosition);
        var teamColor = piece.getTeamColor();
        int movementY = switch (teamColor) {
            case WHITE -> 1;
            case BLACK -> -1;
        };

        if ( outOfBoundsCheck(row, movementY) ){
            var pendingSquare = new ChessPosition(row + movementY, col);

            if (board.getPiece(pendingSquare) == null){ // if there's no piece in the 1st square ahead, add as an available move
                results.add(new ChessMove(myPosition, pendingSquare, null));

                if ( onJumpSpot(myPosition, teamColor) && (outOfBoundsCheck(row, 2*movementY)) ){ // For moving 2 squares ahead on pawn's first move
                    var pendingSquare2 = new ChessPosition(row + 2*movementY, col);

                    if (board.getPiece(pendingSquare2) == null){ // if there's no piece in the 2nd square ahead, add it as an available move
                        results.add(new ChessMove(myPosition, pendingSquare2, null));
                    }
                }
            }

            diagonalCapture(board, teamColor, 1, movementY, row, col, myPosition, results); // DIAGONAL RIGHT (from white's view)
            diagonalCapture(board, teamColor, -1, movementY, row, col, myPosition, results); // DIAGONAL LEFT
        }

        if ( onPromotionSpot(row, ) )

        return results;
    }


    private boolean outOfBoundsCheck(int row, int movementY){
        if ( movementY > 0 ){
            return row + movementY < 9;
        }
        else return row + movementY > 0;

    }

    private boolean onJumpSpot(ChessPosition myPosition, ChessGame.TeamColor teamColor){
        int row = myPosition.getRow();

        return switch (teamColor) {
            case WHITE -> row == 2;
            case BLACK -> row == 7;
        };
    }

    private void diagonalCapture(ChessBoard board, ChessGame.TeamColor teamColor, int colAdd,
                                 int movementY, int row, int col,
                                 ChessPosition myPosition, Collection<ChessMove> results){
        if (col + colAdd < 9){ // DIAGONAL CAPTURE
            var pendingDiagonalSquare = new ChessPosition(row + movementY, col + colAdd);
            var pendingPiece = board.getPiece(pendingDiagonalSquare);

            if ( (pendingPiece != null) && opposingTeamsCheck(teamColor, pendingPiece) ){ // if there is an enemy piece there, capture
                results.add(new ChessMove(myPosition, pendingDiagonalSquare, null));
            }
        }
    }

    private boolean opposingTeamsCheck(ChessGame.TeamColor teamColor, ChessPiece pendingPiece){
        return teamColor != pendingPiece.getTeamColor();
    }

    private boolean onPromotionSpot(int row, )

}
