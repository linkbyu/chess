package model.params;

import chess.ChessGame;

public record JoinRequest(ChessGame.TeamColor playerColor, int gameID) {
}
