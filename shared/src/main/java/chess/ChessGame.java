package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private boolean beenInCheck;

    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
        beenInCheck = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) return null;
        else {
            Collection<ChessMove> validMoves = piece.pieceMoves(board, startPosition);
            TeamColor pieceColor = piece.getTeamColor();

            Collection<ChessMove> removeArray = new ArrayList<>(); //remove from validMoves list
            for (ChessMove move : validMoves){ // for each possible move,
                if ( isNotValid(move, startPosition, pieceColor) ) // remove the invalid ones
                    removeArray.add(move);
            }
            for (var move : removeArray)
                validMoves.remove(move);

            return validMoves;
        }
    }

    private boolean isNotValid(ChessMove move, ChessPosition startPosition, TeamColor teamColor){
        var boardCopy = (ChessBoard) board.clone();
        movePiece(boardCopy, move, startPosition);
        return wouldBeInCheck(boardCopy, teamColor);
    } // isValid calls movePiece and isInCheck


    private void movePiece(ChessBoard boardCopy, ChessMove move, ChessPosition startPosition){
        ChessPiece piece = boardCopy.getPiece(startPosition);
        if (piece != null){
            ChessPosition endPosition = move.getEndPosition();

            boardCopy.addPiece(startPosition, null); // replace where it is with null
            boardCopy.addPiece(endPosition, piece); // add to where it's going
        }
        else throw new RuntimeException("Trying to move a non-existent piece");
    }

    private boolean wouldBeInCheck(ChessBoard board, TeamColor teamColor){
        ChessPosition kingPosition = findKing(board, teamColor);

        TeamColor opposingTeam = switch(teamColor){
            case WHITE -> TeamColor.BLACK;
            case BLACK -> TeamColor.WHITE;
        };
        Collection<ChessPosition> opposingTeamPositions = findTeamPieces(board, opposingTeam);

        // cycle through opposingTeam's pieces to see if one of their pieces has a move
        // that ends on the King's Position
        for (var piecePosition : opposingTeamPositions){
            ChessPiece opposingPiece = board.getPiece(piecePosition);

            var opposingPieceMoves = opposingPiece.pieceMoves(board, piecePosition);
            for (ChessMove opposingMove : opposingPieceMoves){
                ChessPosition endPosition = opposingMove.getEndPosition();
                if ( endPosition.equals(kingPosition) ){
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException { // like a GameTurn method
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor currentTeamTurn = getTeamTurn();


        if ( piece != null && (piece.getTeamColor() == currentTeamTurn) && validMoves(startPosition).contains(move) ) { // if move in validMoves,
            movePiece(board, move, startPosition); // move the piece on the actual board

            // Pawn Promotions
            var promotionPiece = move.getPromotionPiece();
            if ( promotionPiece != null ){
                piece.setPieceType(promotionPiece);
            }

            // Switch turns
            switch(currentTeamTurn){
                case WHITE -> setTeamTurn(TeamColor.BLACK);
                case BLACK -> setTeamTurn(TeamColor.WHITE);
            }
        }
        else
            throw new InvalidMoveException(move.toString());

        // EXTRA CREDIT:
        // if (pieceType == KING or ROOK
        //
    }

    /**
     * Finds the position of a team's King piece
     * @param teamColor which team's King to search for
     * @return A ChessPosition where the desired team's King is
     */
    private ChessPosition findKing(ChessBoard board, TeamColor teamColor){
        Collection<ChessPosition> teamPiecePositions = findTeamPieces(board, teamColor);
        for (var piecePosition : teamPiecePositions){
            var piece = board.getPiece(piecePosition);

            if ( piece.getPieceType() == ChessPiece.PieceType.KING)
                return piecePosition;
        }
        throw new RuntimeException("No King of team " + teamColor + " Found on the Board!");
    }

    private Collection<ChessPosition> findTeamPieces(ChessBoard board, TeamColor requestedTeamColor){
        Collection<ChessPosition> teamPiecePositions = new ArrayList<>();

        for (int row = 1; row < 9; row++){
            for (int col = 1; col < 9; col++){
                var currentPosition = new ChessPosition(row, col);
                var piece = board.getPiece(currentPosition);

                if ( (piece != null) && (piece.getTeamColor() == requestedTeamColor) )
                    teamPiecePositions.add(currentPosition);
            }
        }
        return teamPiecePositions;
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return wouldBeInCheck(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(board, teamColor);


        return isInCheck(teamColor) && validMoves(kingPosition).isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(board, teamColor);
        return !isInCheck(teamColor) && validMoves(kingPosition).isEmpty();
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


}

