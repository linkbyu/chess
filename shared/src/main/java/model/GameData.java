package model;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public ChessGame.TeamColor findWhichTeamUserIsOn(String username) {
        if ( username.equals(whiteUsername)) {
            return ChessGame.TeamColor.WHITE;
        }
        else if ( username.equals(blackUsername)) {
            return ChessGame.TeamColor.BLACK;
        }
        else { // they're an observer
            return null;
        }
    }

    public void invalidMoveCheck(ChessMove requestedMove, String username) throws InvalidMoveException {
        if (game.getGameStatusInfo().isGameOver()) { // check if game is already over
            throw new InvalidMoveException("Game is already over.");
        }

        // attempt requested move
        var userTeamColor = findWhichTeamUserIsOn(username);
        if (userTeamColor == null) { // person is an observer
            throw new InvalidMoveException("Observers cannot participate in the game!");
        }
        ChessPiece requestedPiece = game.getBoard().getPiece(requestedMove.getStartPosition());
        if (requestedPiece == null) { // piece does not exist
            throw new InvalidMoveException("No piece found at the starting position.");
        }
        if (userTeamColor != game.getTeamTurn()) { // tried to move when it's not the player's turn
            throw new InvalidMoveException("It is not your turn!");
        }


        if (userTeamColor == requestedPiece.getTeamColor()) { // check if they're moving their own team's pieces
            try {
                game.makeMove(requestedMove);
            } catch (InvalidMoveException e) {
                throw new InvalidMoveException(String.format("Invalid move with requested piece %s", requestedPiece.getPieceType()));
            }
        } else {
            throw new InvalidMoveException("You cannot move the opposing team's pieces!");
        }
    }

    public void invalidResignCheck(ChessGame.TeamColor teamColor) throws InvalidMoveException {
        var gameStatusInfo = game.getGameStatusInfo();
        if (gameStatusInfo.isGameOver()) { // check if game is already over
            throw new InvalidMoveException("Game is already over.");
        }
        if (teamColor == null) { // the user is an observer
            throw new InvalidMoveException("Observers cannot participate in the game!");
        }
    }
}
