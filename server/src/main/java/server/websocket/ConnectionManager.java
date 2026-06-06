package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashSet;

public class ConnectionManager {

    final HashSet<Session> connections = new HashSet<>();

    void add(Session session) {
        connections.add(session);
    }

    void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(NotificationMessage notification, Session excludedSession) throws IOException {
        String msg = notification.getMessage();

        for (Session session : connections) {
            if (session.isOpen() && !session.equals(excludedSession)) {
                session.getRemote().sendString(msg);
            }
        }
    }


}
