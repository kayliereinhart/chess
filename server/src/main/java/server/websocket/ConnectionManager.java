package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer id, Session session) {
        if (connections.containsKey(id)) {
            connections.get(id).add(session);
        } else {
            HashSet<Session> sessions = new HashSet<>();
            sessions.add(session);
            connections.put(id, sessions);
        }
    }

    public void remove(Integer id, Session session) {
        connections.get(id).remove(session);
    }

    public void broadcast(Integer id, Session excludeSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        for (Session c : connections.get(id)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
