package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.authdatabase.AuthToken;
import dataaccess.gamedatabase.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Service;
import websocket.commands.JoinGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {
  private final ConnectionManager connections = new ConnectionManager();
  private final Service service;

  public void connect(String message, Session session) throws IOException {
    JoinGameCommand command = new Gson().fromJson(message, JoinGameCommand.class);
    connections.add(command.getAuthToken(), session);
    String name = service.getDB().getSession(command.getAuthToken()).username();
    if (command.getColor() == null) {
      String messageReturn=String.format("%s has joined your game", name);
      var notification=new Notification(ServerMessage.ServerMessageType.NOTIFICATION, messageReturn);
      connections.broadcast(command.getAuthToken(), notification);
    }else{
      String messageReturn=String.format("%s has joined your game as %s", name, command.getColor());
      var notification=new Notification(ServerMessage.ServerMessageType.NOTIFICATION, messageReturn);
      connections.broadcast(command.getAuthToken(), notification);
    }
  }

  public void leave(String authToken) throws IOException {
    connections.remove(authToken);
    String name = service.getDB().getSession(authToken).username();
    String message = String.format("%s has left your game", name);
    var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
    connections.broadcast(authToken, notification);
  }

  public void resign(String authToken,int gameID) throws IOException {
    String name = service.getDB().getSession(authToken).username();
    ChessGame game = service.getDB().getGame(gameID).game();
    game.resign(game.getTeamTurn());
    String message = String.format("%s has resigned", name);
    var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
    connections.broadcast(authToken, notification);
  }


  public WebSocketHandler(Service service){
    this.service = service;
  }

  @OnWebSocketMessage
  public void onMessage(String message, Session session) throws IOException {
    UserGameCommand command=  new Gson().fromJson(message, UserGameCommand.class);
    AuthToken token = service.getDB().getSession(command.getAuthToken());
    GameData game = service.getDB().getGame(command.getGameID());
    if (token == null){
      session.getRemote().sendString("error invalid token");
      return;
    }
    if (game== null){
      session.getRemote().sendString("error invalid GameID");
      return;
    }

    switch (command.getCommandType()) {
      case RESIGN -> resign(command.getAuthToken(), command.getGameID());
      case CONNECT -> connect(message, session);
      case LEAVE -> leave(command.getAuthToken());
      case MAKE_MOVE -> makeMove(message);
    }
  }

  private void makeMove(String message) throws IOException {
    MakeMoveCommand command=  new Gson().fromJson(message, MakeMoveCommand.class);
    ChessGame game = service.getDB().getGame(command.getGameID()).game();
    try {
      game.makeMove(command.getMove());
    } catch (InvalidMoveException e) {
      String errorM = String.format("%s is invalid", command.getMove().toString());
      connections.sendMessage(command.getAuthToken(), new Error(ServerMessage.ServerMessageType.ERROR, errorM));
      return;
    }
    String name = service.getDB().getSession(command.getAuthToken()).username();
    String note = String.format("%s has left your game", name);
    var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, note);
    connections.broadcast(command.getAuthToken(), notification);
    var move = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
    connections.broadcast(command.getAuthToken(), move);
  }


}
