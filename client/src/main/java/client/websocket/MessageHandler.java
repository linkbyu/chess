package client.websocket;

import websocket.messages.NotificationMessage;

public interface MessageHandler {
    void notify(NotificationMessage notificationMessage);
}
