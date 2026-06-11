package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.UnauthorizedException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService;
    private final GameService gameService;


    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }


    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws DataAccessException, UnauthorizedException, IOException {
        UserGameCommand userCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        try {
            var authData = userService.verifyAuth( userCommand.getAuthToken() );
            String username = authData.username();

            var gameData = gameService.findGame(userCommand.getGameID());
            var commandType = userCommand.getCommandType();

            switch (commandType) {
                case CONNECT -> connect(ctx.session, gameData, username);
                case MAKE_MOVE -> {
                    var makeMoveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(ctx.session, gameData, username, makeMoveCommand.getMove());
                }
                case RESIGN -> resign(ctx.session, gameData, username);
                case LEAVE -> leave(ctx.session, gameData, username);
            }
        } catch (IOException ex) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                                        "A Connection Error Has Occurred.");
            sendServerMessage(ctx.session, error);
        } catch (DataAccessException | UnauthorizedException ex) {
            sendServerMessage(ctx.session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage()));
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) {
        System.out.println("Websocket closed");
    }

    private void connect(Session rootSession, GameData gameData, String username) throws IOException, DataAccessException {
        connections.add(gameData.gameID(), rootSession);

        String msg = String.format("%s has joined the ", username);
        if (username.equals(gameData.whiteUsername())) {
            msg += "White Team.";
        } else if (username.equals(gameData.blackUsername())) {
            msg += "Black Team.";
        } else { // observer
            msg = String.format("%s is observing the game.", username);
        }

        // broadcast notification to everyone else in the game
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcast(gameData.gameID(), notification, rootSession);

        // LoadGame Message to Root User
        sendServerMessage(rootSession, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData, null));
    }

    private void sendServerMessage(Session rootSession, ServerMessage serverMessage) throws IOException {
        String jsonString = new Gson().toJson(serverMessage);
        if (rootSession.isOpen()) {
            rootSession.getRemote().sendString(jsonString);
        }
    }

    private void makeMove(Session rootSession, GameData gameData, String username, ChessMove requestedMove) throws IOException, DataAccessException {
        ChessGame game = gameData.game();
        try {
            if (game.getGameStatusInfo().isGameOver()) { // check if game is already over
                throw new InvalidMoveException("Game is already over.");
            }

            // attempt requested move
            var userTeamColor = findWhichTeamUserIsOn(gameData, username);
            if ( userTeamColor == null ) { // person is an observer
                throw new InvalidMoveException("Observers cannot participate in the game!");
            }
            ChessPiece requestedPiece = game.getBoard().getPiece(requestedMove.getStartPosition());
            if (requestedPiece == null) { // piece does not exist
                throw new InvalidMoveException("No piece found at the starting position.");
            }
            if ( userTeamColor != game.getTeamTurn() ) { // tried to move when it's not the player's turn
                throw new InvalidMoveException("It is not your turn!");
            }



            if ( userTeamColor == requestedPiece.getTeamColor() ) { // check if they're moving their own team's pieces
                try {
                    game.makeMove(requestedMove);
                } catch (InvalidMoveException e) {
                    throw new InvalidMoveException(String.format("Invalid move with requested piece %s", requestedPiece.getPieceType()) );
                }
            }
            else {
                throw new InvalidMoveException("You cannot move the opposing team's pieces!");
            }

            // See if there is a check, checkmate, or stalemate
            broadcastGameStateChange(game, gameData, ChessGame.TeamColor.WHITE, username);
            broadcastGameStateChange(game, gameData, ChessGame.TeamColor.BLACK, username);

            // update database
            int gameID = gameData.gameID();
            GameData updatedGameData = updateGameToDatabase(gameID, gameData, game);

            // load the updated game for everyone
            connections.loadGameForAllClients(gameID,
                    new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, updatedGameData, requestedMove));

            // notify all other clients what move was made and by whom
            String msg = String.format("%s made the move %s %s", username, requestedPiece, requestedMove);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
            connections.broadcast(gameID, notification, rootSession);


        } catch (InvalidMoveException ex) {
            var invalidMoveError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            sendServerMessage(rootSession, invalidMoveError);
        }
    }


    private GameData updateGameToDatabase(int gameID, GameData gameData, ChessGame updatedGame) throws DataAccessException {
        var updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), updatedGame);
        gameService.updateGame(gameID, updatedGameData);

        return updatedGameData;
    }


    private void broadcastGameStateChange(ChessGame game, GameData oldGameData, ChessGame.TeamColor teamColor, String username) throws IOException {
        String gameStateMsg = createGameStateMessage(game, oldGameData, teamColor, username);

        if ( !gameStateMsg.isEmpty() ) { // game state has changed
            var gameStateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, gameStateMsg);
            connections.broadcast(oldGameData.gameID(), gameStateNotification, null);
        }
    }

    private String createGameStateMessage(ChessGame game, GameData oldGameData, ChessGame.TeamColor teamColor, String username) {
        String msg = "";
        if ( game.isInCheckmate(teamColor) ) {
            game.setGameStatusInfo(new GameStatusInfo(GameStatusInfo.GameStatus.CHECKMATE, oldGameData.whiteUsername(),
                                                      oldGameData.blackUsername(), teamColor));

            msg = String.format("%s Team is in Checkmate.", teamColor);
        }
        else if ( game.isInCheck(teamColor) ) {
            game.setGameStatusInfo(new GameStatusInfo(GameStatusInfo.GameStatus.CHECK, oldGameData.whiteUsername(),
                    oldGameData.blackUsername(), teamColor));
            msg = String.format("%s Team is in Check.", teamColor);
        }
        else if ( game.isInStalemate(teamColor) ) {
            game.setGameStatusInfo(new GameStatusInfo(GameStatusInfo.GameStatus.STALEMATE, oldGameData.whiteUsername(),
                    oldGameData.blackUsername(), teamColor));
            msg = String.format("%s Team is in a Stalemate.", teamColor);
        }

        return msg;
    }

    private void resign(Session rootSession, GameData gameData, String username) throws IOException, DataAccessException {
        try {
            ChessGame game = gameData.game();
            var gameStatusInfo = game.getGameStatusInfo();
            if (gameStatusInfo.isGameOver()) { // check if game is already over
                throw new InvalidMoveException("Game is already over.");
            }
            var teamColor = findWhichTeamUserIsOn(gameData, username);
            if (teamColor == null) { // the user is an observer
                throw new InvalidMoveException("Observers cannot participate in the game!");
            }

            // the user is a player and game hasn't ended yet, so mark game as over
            game.setGameStatusInfo(new GameStatusInfo(GameStatusInfo.GameStatus.CHECKMATE, gameData.whiteUsername(),
                    gameData.blackUsername(), teamColor));

            int gameID = gameData.gameID();
            updateGameToDatabase(gameID, gameData, game);

            // notify all clients that the root player has resigned
            var notification = getResignMessage(gameData, username, teamColor);
            connections.broadcast(gameID, notification, null);

        } catch (InvalidMoveException ex) {
            sendServerMessage(rootSession, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage()));
        }
    }

    private static NotificationMessage getResignMessage(GameData gameData, String username, ChessGame.TeamColor teamColor) {
        String opposingTeamUsername = "";
        if (teamColor == ChessGame.TeamColor.WHITE) {
            opposingTeamUsername = gameData.blackUsername();
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            opposingTeamUsername = gameData.whiteUsername();
        }
        String msg = String.format("%s has forfeit. %s wins!", username, opposingTeamUsername);
        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
    }

    private void leave(Session rootSession, GameData gameData, String username) throws DataAccessException, IOException {
        int gameID = gameData.gameID();

        // update game if they were a player
        var teamColor = findWhichTeamUserIsOn(gameData, username);
        switch (teamColor) {
            case WHITE: {
                var updatedGameData = new GameData(gameID, null, gameData.blackUsername(),
                        gameData.gameName(), gameData.game());
                gameService.updateGame(gameID, updatedGameData);
                break;
            }
            case BLACK: {
                var updatedGameData = new GameData(gameID, gameData.whiteUsername(), null,
                        gameData.gameName(), gameData.game());
                gameService.updateGame(gameID, updatedGameData);
                break;
            }
            case null:

        }

        connections.remove(gameID, rootSession);

        // alert all other clients that the root client left (both observers and players)
        String msg = String.format("%s has left the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcast(gameID, notification, null);
    }

    private ChessGame.TeamColor findWhichTeamUserIsOn(GameData gameData, String username) {
        if ( username.equals( gameData.whiteUsername()) ) {
            return ChessGame.TeamColor.WHITE;
        }
        else if ( username.equals( gameData.blackUsername()) ) {
            return ChessGame.TeamColor.BLACK;
        }
        else { // they're an observer
            return null;
        }
    }

    /*private String findOpposingTeamPlayer(String whiteUsername, String blackUsername, String username) {
        if ( username.equals( whiteUsername ) ) {
            return blackUsername;
        }
        else if ( username.equals( blackUsername ) ) {
            return whiteUsername;
        }
        else { // they're an observer
            return null;
        }
    }*/
}
