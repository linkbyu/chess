package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalc implements PieceMovesCalc{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> results = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        var teamColor = piece.getTeamColor();
        int movementY = switch(teamColor){
            case WHITE -> 1;
            case BLACK -> -1;
        };

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int forwardRow = row + movementY;


        if ( forwardRow < 9 && forwardRow > 0 ){ // OutOfBounds Check for vertical movement
            var pendingSquare = new ChessPosition(forwardRow, col);
            var pendingPiece = board.getPiece(pendingSquare);

            if ( pendingPiece == null ){ // no piece in front
                if ( onPromotionSpot(teamColor, forwardRow) ){ // check if it's a promotionSpot
                    promotion(myPosition, pendingSquare, results);
                }
                else {
                    results.add(new ChessMove(myPosition, pendingSquare, null));
                }


                // Option to move 2 squares ahead at the start of the game
                if ( onJumpSpot(teamColor, row) ){
                    var pendingSquare2 = new ChessPosition(row + 2*movementY, col);
                    ChessPiece pendingPiece2 = board.getPiece(pendingSquare2);

                    if ( pendingPiece2 == null ){ // no piece 2 squares ahead, add it as an available move
                        results.add(new ChessMove(myPosition, pendingSquare2, null));
                    }

                }


            }

            diagonalCapture(board, myPosition, teamColor, forwardRow, col, 1, results); // RIGHT DIAGONAL (from White's view)
            diagonalCapture(board, myPosition, teamColor, forwardRow, col, -1, results); // LEFT DIAGONAL
        }

        return results;
    }


    private boolean onPromotionSpot(ChessGame.TeamColor teamColor, int row) {
        return switch(teamColor){
            case WHITE -> row == 8;
            case BLACK -> row == 1;
        };

    }

    private void promotion(ChessPosition myPosition, ChessPosition pendingSquare, Collection<ChessMove> results) {
        results.add(new ChessMove(myPosition, pendingSquare, ChessPiece.PieceType.QUEEN));
        results.add(new ChessMove(myPosition, pendingSquare, ChessPiece.PieceType.BISHOP));
        results.add(new ChessMove(myPosition, pendingSquare, ChessPiece.PieceType.ROOK));
        results.add(new ChessMove(myPosition, pendingSquare, ChessPiece.PieceType.KNIGHT));

    }

    private boolean onJumpSpot(ChessGame.TeamColor teamColor, int row) {
        return switch(teamColor){
            case WHITE -> row == 2;
            case BLACK -> row == 7;
        };
    }

    private void diagonalCapture(ChessBoard board, ChessPosition myPosition,
                                 ChessGame.TeamColor teamColor,
                                 int row, int col,
                                 int movementX,
                                 Collection<ChessMove> results) {
        col += movementX;

        if ( col < 9 && col > 0 ){ // OutOfBounds Check for column
            var pendingSquare = new ChessPosition(row, col);
            var pendingPiece = board.getPiece(pendingSquare);

            if ( (pendingPiece != null) && opposingTeamsCheck(teamColor, pendingPiece) ){
                // If there's an enemy Piece on a diagonal Square, add capturing as an available move
                if ( onPromotionSpot(teamColor, row) ){ // check if it's a promotionSpot
                    promotion(myPosition, pendingSquare, results);
                }
                else {
                    results.add(new ChessMove(myPosition, pendingSquare, null));
                }
            }
        }
    }

    static boolean opposingTeamsCheck(ChessGame.TeamColor teamColor, ChessPiece pendingPiece) {
        return pendingPiece.getTeamColor() != teamColor;
    }


}
