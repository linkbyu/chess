package websocket.messages;

public class NotificationMessage extends ServerMessage {

    private String message;
    private boolean fill;

    public NotificationMessage(ServerMessageType type, String message, boolean fill) {
        super(type);
        this.message = message;
        this.fill = fill;
    }

    public String getMessage() {
        return message;
    }

    public boolean isFill() {
        return fill;
    }

}
