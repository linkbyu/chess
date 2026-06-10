package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private MessageHandler messageHandler;

    public WebSocketFacade(String url, MessageHandler messageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.messageHandler = messageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, socketURI);


            session.addMessageHandler(new jakarta.websocket.MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    var notification = new Gson().fromJson(message, NotificationMessage.class);
                    messageHandler.notify(notification);
                }
            });

        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        //Endpoint requires this method, but you don't have to do anything
    }

    public void leaveGame(String authToken, int gameID) throws ResponseException {
        try {
            var leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove requestedMove) throws ResponseException {
        try {
            var moveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, requestedMove);
            this.session.getBasicRemote().sendText(new Gson().toJson(moveCommand));

        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void resignGame(String authToken, int gameID) throws ResponseException {
        try {
            var resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(resignCommand));

        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

}
