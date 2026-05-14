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
    private boolean enPassantWhiteTurn;
    private boolean enPassantBlackTurn;

    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
        enPassantWhiteTurn = false;
        enPassantBlackTurn = false;

        board.resetBoard();
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

        if (enPassantWhiteTurn || enPassantBlackTurn)
            switch(team){
                case WHITE -> enPassantBlackTurn = false;
                case BLACK -> enPassantWhiteTurn = false;
            }
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
                if ( ifMoveThenCheck(move, pieceColor) ) // remove the invalid ones
                    removeArray.add(move);
            }
            for (var move : removeArray)
                validMoves.remove(move);
            ChessPiece.PieceType pieceType = piece.getPieceType();


            // ADD CASTLING MOVES IF VALID
            if ( (pieceType == ChessPiece.PieceType.KING) && piece.getHasNotMoved() )
                addValidCastleMoves(validMoves, pieceColor, startPosition);

            // ADD EN PASSANT IF VALID
            if ( (pieceType == ChessPiece.PieceType.PAWN) && verifyEnPassantTurn(pieceColor) )
                addValidEnPassant(validMoves, startPosition);


            return validMoves;
        }
    }

    private boolean ifMoveThenCheck(ChessMove move, TeamColor teamColor){
        var boardCopy = (ChessBoard) board.clone();
        movePiece(boardCopy, move);
        return wouldBeInCheck(boardCopy, teamColor);
    }


    private void movePiece(ChessBoard boardCopy, ChessMove move){
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = boardCopy.getPiece(startPosition);
        if (piece != null){
            ChessPosition endPosition = move.getEndPosition();

            boardCopy.addPiece(startPosition, null); // replace where it is with null
            boardCopy.addPiece(endPosition, piece); // add to where it's going

            var pieceType = piece.getPieceType();
            boolean pieceJustMoved = piece.getHasNotMoved();

            // IF MOVE CASTLED KING, MOVE ROOK
            if ( ( pieceType == ChessPiece.PieceType.KING) && pieceJustMoved )
                moveRookAfterCastle(boardCopy, move, piece);

            boolean pieceIsAPawn = pieceType == ChessPiece.PieceType.PAWN;
            // IF THE MOVE IS AN EN PASSANT, REMOVE THE CAPTURED PAWN
            if ( (pieceIsAPawn) && verifyEnPassantMove(move) )
                boardCopy.addPiece(new ChessPosition(startPosition.getRow(), endPosition.getColumn()), null);


            // IF PAWN MOVED 2 FORWARD, ALLOW EN PASSANT FOR OPPOSING TEAM NEXT TURN
            if ( (pieceIsAPawn) && verifyPawn2Forward(move) && pieceJustMoved ) {
                switch(piece.getTeamColor()){
                    case WHITE -> enPassantBlackTurn = true;
                    case BLACK -> enPassantWhiteTurn = true;
                }
                piece.setDid2Forward(true);
            }

            piece.hasMoved();
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
                if ( endPosition.equals(kingPosition) )
                    return true;
            }
        }
        return false;
    }

    private void addValidCastleMoves(Collection<ChessMove> validMoves, TeamColor teamColor, ChessPosition startPosition){
        int row = getBackRowNum(teamColor);

        ChessPiece kingSideRook = board.getPiece(new ChessPosition(row, 8));
        ChessPiece queenSideRook = board.getPiece(new ChessPosition(row, 1));

        // KING'S SIDE
        if ( rookHasNotMoved(kingSideRook) && roomForCastle(row, true)
                && noCheckDuringCastle(row, teamColor, startPosition, true) ){
            validMoves.add(new ChessMove(startPosition, new ChessPosition(row, 7), null));
        }

        // QUEEN'S SIDE
        if ( rookHasNotMoved(queenSideRook) && roomForCastle(row, false) && noCheckDuringCastle(row, teamColor, startPosition, false) ){
            validMoves.add(new ChessMove(startPosition, new ChessPosition(row, 3), null));
        }
    }

    private boolean rookHasNotMoved(ChessPiece rook){
        return rook != null && (rook.getPieceType() == ChessPiece.PieceType.ROOK)
                && rook.getHasNotMoved();
    }

    private boolean roomForCastle(int row, boolean kingSide){
        if ( kingSide ){
            return (board.getPiece(new ChessPosition(row, 6)) == null)
                    && (board.getPiece(new ChessPosition(row, 7)) == null);
        }
        else return board.getPiece(new ChessPosition(row, 4)) == null
                && board.getPiece(new ChessPosition(row, 3)) == null
                && board.getPiece(new ChessPosition(row, 2)) == null;
    }

    private boolean noCheckDuringCastle(int row, TeamColor teamColor, ChessPosition kingPosition, boolean kingSide){
        int colIter;
        if ( kingSide ) colIter = 1;
        else colIter = -1;

        var king1SpaceMove = new ChessMove(kingPosition, new ChessPosition(row, kingPosition.getColumn() + colIter), null );
        var castleMove = new ChessMove(kingPosition, new ChessPosition(row, kingPosition.getColumn() + 2*colIter), null);

        return !isInCheck(teamColor) && !ifMoveThenCheck(king1SpaceMove, teamColor)
                && !ifMoveThenCheck(castleMove, teamColor);
    }

    private void moveRookAfterCastle(ChessBoard boardCopy, ChessMove kingMove, ChessPiece piece){
        int row = getBackRowNum(piece.getTeamColor());

        var standardKingPosition = new ChessPosition(row, 5);

        var kingSideCastleMove = new ChessMove(standardKingPosition, new ChessPosition(row, 7), null);
        var queenSideCastleMove = new ChessMove(standardKingPosition, new ChessPosition(row, 3), null);

        // KING'S SIDE
        if ( kingMove.equals(kingSideCastleMove) ){
            var rookPosition = new ChessPosition(row, 8);
            movePiece(boardCopy, new ChessMove(rookPosition, new ChessPosition(row, 6), null));
        }

        // QUEEN'S SIDE
        if ( kingMove.equals(queenSideCastleMove) ){
            var rookPosition = new ChessPosition(row, 1);
            movePiece(boardCopy, new ChessMove(rookPosition, new ChessPosition(row, 4), null));
        }
    }


    private boolean verifyEnPassantTurn(TeamColor pieceColor){
        return switch(pieceColor){
            case WHITE -> enPassantWhiteTurn;
            case BLACK -> enPassantBlackTurn;
        };
    }

    private void addValidEnPassant(Collection<ChessMove> validMoves, ChessPosition pawnPosition){
        verifySidePawn(validMoves, pawnPosition, true);
        verifySidePawn(validMoves, pawnPosition, false);
    }

    /**
     * Verifies that the adjacent pieces are pawns and that they moved 2 forward last turn.
     * Adds the En Passant move to validMoves if verified.
     * @param validMoves A list of valid moves a piece can make that turn
     * @param pawnPosition Current position of the pawn attempting the en passant
     * @param rightSide looks at the adjacent right side if true, and adjacent left side if false
     */
    private void verifySidePawn(Collection<ChessMove> validMoves, ChessPosition pawnPosition, boolean rightSide){
        int row = pawnPosition.getRow();
        int col = pawnPosition.getColumn();

        int colIter;
        boolean withinBounds;
        if ( rightSide ) { // for verifying the right side piece
            colIter = 1;
            withinBounds = col < 8;
        }
        else{ // for verifying the left side piece
            colIter = -1;
            withinBounds = col > 1;
        }


        if ( withinBounds ) {
            ChessPiece sidePiece = board.getPiece(new ChessPosition(row, col + colIter ));

            if (sidePiece != null && sidePiece.getDid2Forward() && (sidePiece.getPieceType() == ChessPiece.PieceType.PAWN) ) {
                // side piece isn't null, did 2 forward, and is a pawn
                TeamColor sidePieceColor = sidePiece.getTeamColor();
                int newRow = switch(sidePieceColor){
                    case WHITE -> row - 1;
                    case BLACK -> row + 1;
                };
                validMoves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, col + colIter), null));
            }
        }
    }

    private boolean verifyEnPassantMove(ChessMove move){
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        int rowStart = startPosition.getRow();
        int rowFinish = endPosition.getRow();
        int colStart = startPosition.getColumn();
        int colFinish = endPosition.getColumn();

        if ( (Math.abs(colFinish - colStart) == 1) && (Math.abs(rowFinish - rowStart) == 1) ){
            var adjacentPiece = board.getPiece(new ChessPosition(rowStart, colFinish));
            return (adjacentPiece != null) && (adjacentPiece.getPieceType() == ChessPiece.PieceType.PAWN);
        }
        else return false;

    }

    private boolean verifyPawn2Forward(ChessMove move){
        int rowFinish = move.getEndPosition().getRow();
        int rowStart = move.getStartPosition().getRow();

        return Math.abs(rowFinish - rowStart) == 2;
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
            movePiece(board, move); // move the piece on the actual board

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
        if ( isInCheck(teamColor) ){
            return verifyNoValidMoves(teamColor);
        }
        else return false;
    }

    private boolean verifyNoValidMoves(TeamColor teamColor){
        Collection<ChessPosition> teamPieces = findTeamPieces(board, teamColor);

        for ( ChessPosition teamPosition : teamPieces ){
            if ( !validMoves(teamPosition).isEmpty() ){
                return false;
            }

        }
        return true; // no valid team moves, they're all empty
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if ( !isInCheck(teamColor) ){
            return verifyNoValidMoves(teamColor);
        }
        else return false;
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

    protected int getBackRowNum(TeamColor teamColor){
        return switch (teamColor) {
            case WHITE -> 1;
            case BLACK -> 8;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return enPassantWhiteTurn == chessGame.enPassantWhiteTurn && enPassantBlackTurn == chessGame.enPassantBlackTurn && Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, enPassantWhiteTurn, enPassantBlackTurn);
    }
}

