package server.websocket;
import server.websocket.Connection;
import websocket.messages.Notification;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
  public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

  public void add(String visitorName, Session session) {
    var connection = new Connection(visitorName, session);
    connections.put(visitorName, connection);
  }

  public void remove(String authToken) {
    connections.remove(authToken);
  }

  public void broadcast(String excludeToken, Notification notification) throws IOException {
    var removeList = new ArrayList<Connection>();
    for (var c : connections.values()) {
      if (c.session.isOpen()) {
        if (!c.authToken.equals(excludeToken)) {
          c.send(notification.toString());
        }
      } else {
        removeList.add(c);
      }
    }

    // Clean up any connections that were left open.
    for (var c : removeList) {
      connections.remove(c.authToken);
    }
  }
}