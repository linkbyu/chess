package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {

    final Map<Integer, Set<Session>> connections = new HashMap<>();



    void add(int gameID, Session session) {
        Set<Session> gameConnections = connections.get(gameID);
        if (gameConnections == null) {
            connections.put(gameID, new HashSet<>() );
            gameConnections = connections.get(gameID);
        }

        gameConnections.add(session);
    }

    void remove(int gameID, Session session) {
        Set<Session> gameConnections = connections.get(gameID);
        gameConnections.remove(session);

        if (gameConnections.isEmpty()) {
            connections.remove(gameID);
        }
    }

    public void broadcast(int gameID, NotificationMessage notification, Session excludedSession) throws IOException {
        String jsonString = new Gson().toJson(notification);

        Set<Session> gameConnections = connections.get(gameID);
        for (Session session : gameConnections) {
            if (session.isOpen()) {
                if (!session.equals(excludedSession)) {
                    session.getRemote().sendString(jsonString);
                }
            }
            else {
                remove(gameID, session); // removing closed connections
            }
        }
    }

    public void loadGameForAllClients(int gameID, LoadGameMessage loadGameMessage) throws IOException {
        String jsonString = new Gson().toJson(loadGameMessage);

        Set<Session> gameConnections = connections.get(gameID);
        for (Session session : gameConnections) {
            if (session.isOpen()) {
                session.getRemote().sendString(jsonString);
            }
            else {
                remove(gameID, session); // removing closed connections
            }
        }
    }


}
