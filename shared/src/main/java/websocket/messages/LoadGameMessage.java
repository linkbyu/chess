package websocket.messages;

import chess.ChessMove;
import model.GameData;

public class LoadGameMessage extends ServerMessage{

    private GameData game;
    private ChessMove previousMove;

    public LoadGameMessage(ServerMessageType type, GameData gameData, ChessMove previousMove) {
        super(type);
        this.game = gameData;
        this.previousMove = previousMove;
    }


    public GameData getGameData() {
        return game;
    }

    public ChessMove getPreviousMove() {
        return previousMove;
    }
}
