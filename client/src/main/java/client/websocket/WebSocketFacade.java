package client.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.messages.NotificationMessage;

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

}
