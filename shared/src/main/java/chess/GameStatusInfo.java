package chess;

public record GameStatusInfo(GameStatus gameStatus, String whiteUsername, String blackUsername, ChessGame.TeamColor affectedTeam) {
    public enum GameStatus {
        NEW_GAME,
        ONGOING,
        CHECK,
        CHECKMATE,
        STALEMATE
    }

    public boolean isGameOver() {
        return switch(gameStatus) {
            case NEW_GAME, ONGOING, CHECK -> false;
            case CHECKMATE, STALEMATE -> true;
        };
    }

    public String getAffectedPlayer() {
        return switch(affectedTeam) {
            case WHITE -> whiteUsername;
            case BLACK -> blackUsername;
            case null -> null;
        };
    }

    public String getOtherPlayer() {
        return switch(affectedTeam) {
            case WHITE -> blackUsername;
            case BLACK -> whiteUsername;
            case null -> null;
        };
    }

    public ChessGame.TeamColor getStatusChangerTeam() {
        return switch(affectedTeam) {
            case WHITE -> ChessGame.TeamColor.BLACK;
            case BLACK -> ChessGame.TeamColor.WHITE;
            case null -> null;
        };
    }
}
