package client.websocket;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface MessageHandler {
    void notify(NotificationMessage notificationMessage);
    void loadGame(LoadGameMessage loadGameMessage);
    void showError(ErrorMessage errorMessage);
}
