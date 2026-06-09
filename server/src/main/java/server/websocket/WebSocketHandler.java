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
        var authData = userService.verifyAuth( userCommand.getAuthToken() );
        String username = authData.username();

        var gameData = gameService.findGame(userCommand.getGameID());

        switch (userCommand.getCommandType()) {
            case CONNECT -> connect( ctx.session, gameData, username );
            case MAKE_MOVE -> makeMove(ctx.session, gameData, username, ((MakeMoveCommand) userCommand).getMove());
            case RESIGN -> resign(gameData, username);
            case LEAVE -> leave(ctx.session, gameData, username);
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) {
        System.out.println("Websocket closed");
    }

    private void connect(Session rootSession, GameData gameData, String username) throws IOException {
        connections.add(rootSession);

        String msg = String.format("%s has joined the ", username);
        if ( username.equals(gameData.whiteUsername()) ) {
            msg += "White Team.";
        } else if ( username.equals( gameData.blackUsername()) ) {
            msg += "Black Team.";
        }
        else { // observer
            msg = String.format("%s is observing the game.", username);
        }

        // broadcast notification to everyone else in the game
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcast(notification, rootSession);

        loadGameToRootUser(rootSession, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData));
    }

    private void loadGameToRootUser(Session rootSession, LoadGameMessage loadGameMessage) throws IOException {
        String jsonString = new Gson().toJson(loadGameMessage);
        rootSession.getRemote().sendString(jsonString);
    }

    private void makeMove(Session rootSession, GameData gameData, String username, ChessMove requestedMove) {
        ChessGame game = gameData.game();
        try {
            // attempt requested move
            game.makeMove(requestedMove);

            // update database
            int gameID = gameData.gameID();
            var updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), game);
            gameService.updateGame(gameID, updatedGameData);

            // load the updated game for everyone
            connections.loadGameForAllClients(new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, updatedGameData));

            // notify all other clients what move was made and by whom
            String msg = createMoveMessage(username, game, requestedMove);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
            connections.broadcast(notification, rootSession);

            // See if there is a check, checkmate, or stalemate
            broadcastGameStateChange(game, ChessGame.TeamColor.WHITE);
            broadcastGameStateChange(game, ChessGame.TeamColor.BLACK);


        } catch (InvalidMoveException ex) {
            throw new RuntimeException(ex);
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createMoveMessage(String username, ChessGame game, ChessMove move) {
        ChessPiece movedPiece = game.getBoard().getPiece(move.getEndPosition());
        String msg = String.format("%s made the move %s", username, movedPiece.toString() );

        String startingPos = readablePositionOnBoard( move.getStartPosition() );
        String endingPos = readablePositionOnBoard( move.getEndPosition() );
        msg += String.format(" %s %s", startingPos, endingPos);

        return msg;
    }

    private String readablePositionOnBoard(ChessPosition position) {
        String boardPos = switch( position.getColumn() ) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> "a";
        };

        boardPos += position.getRow();
        return boardPos;
    }

    private void broadcastGameStateChange(ChessGame game, ChessGame.TeamColor teamColor) throws IOException {
        String gameStateMsg = createGameStateMessage(game, teamColor);

        if ( !gameStateMsg.isEmpty() ) {
            var gameStateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, gameStateMsg);
            connections.broadcast(gameStateNotification, null);
        }
    }

    private String createGameStateMessage(ChessGame game, ChessGame.TeamColor teamColor) {
        String msg = "";
        if ( game.isInCheckmate(teamColor) ) {
            msg = String.format("%s Team is in Checkmate.", teamColor);
        }
        else if ( game.isInCheck(teamColor) ) {
            msg = String.format("%s Team is in Check.", teamColor);
        }
        else if ( game.isInStalemate(teamColor) ) {
            msg = String.format("%s Team is in a Stalemate.", teamColor);
        }

        return msg;
    }

    private void resign(GameData gameData, String username) throws IOException, InvalidMoveException {
        // mark game as over
        var teamColor = findWhichTeamUserIsOn(gameData, username);
        if (teamColor == null) {
            throw new InvalidMoveException("You cannot resign as an observer!");
        }

        // notify all clients that the root player has resigned
        String opposingTeamUsername = "";
        if (teamColor == ChessGame.TeamColor.WHITE) {
            opposingTeamUsername = gameData.blackUsername();
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            opposingTeamUsername = gameData.whiteUsername();
        }
        String msg = String.format("%s has forfeit. %s wins!", username, opposingTeamUsername);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcast(notification, null);
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

        connections.remove(rootSession);

        // alert all other clients that the root client left (both observers and players)
        String msg = String.format("%s has left the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcast(notification, null);
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
}
