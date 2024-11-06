package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Service;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {
  private final ConnectionManager connections = new ConnectionManager();
  private Service service;

  public void connect(String authtoken, Session session){
    connections.add(authtoken, session);
    String name = service.getDB().getSession(authtoken).username();
    String message = String.format("%s has joined your game", name);
    var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
    connections.broadcast(name, notification);
  }

  public void leave(String authToken, Session session){
    connections.remove(authToken, session);
    String name = service.getDB().getSession(authToken).username();
    String message = String.format("%s has left your game", name);
    var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
    connections.broadcast(name, notification);

  }




  public WebSocketHandler(Service service){
    this.service = service;
  }


  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException {
    UserGameCommand command=  new Gson().fromJson(message, UserGameCommand.class);
    switch (command.getCommandType()) {
      case UserGameCommand.CommandType.RESIGN -> resign(command.getAuthToken(), session);
      case CONNECT -> connect(command.getAuthToken(), session);
      case MAKE_MOVE -> makeMove();
      case LEAVE -> leave();
    }
  }

}
