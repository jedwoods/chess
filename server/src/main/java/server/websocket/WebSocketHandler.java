package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.authdatabase.AuthToken;
import dataaccess.gamedatabase.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
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
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
  private final ConnectionManager connections = new ConnectionManager();
  private final Service service;

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




  //  @OnWebSocketConnect
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
    GameData chessGame = service.getDB().getGame(gameID);
    ChessGame game =chessGame.game();


      String winn;
      if (game.getTeamTurn() == ChessGame.TeamColor.WHITE){
        winn =chessGame.blackUsername();
        service.getDB().addWinner(chessGame, chessGame.blackUsername());
      }
      else{
        winn =chessGame.whiteUsername();
        service.getDB().addWinner(chessGame, chessGame.blackUsername());
      }
      Notification winMess = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has resigned", winn));
      connections.broadcast(authToken, winMess);
      connections.sendMessage(authToken, winMess);
  }


  private void makeMove(String message) throws IOException {

    MakeMoveCommand command=new Gson().fromJson(message, MakeMoveCommand.class);
    ChessGame game=service.getDB().getGame(command.getGameID()).game();
    GameData chessGame = service.getDB().getGame(command.getGameID());
    String winner = service.getDB().getWinner(chessGame.gameID());
    if (!Objects.equals(winner, "NULL")){
      String observe = String.format("%s has already won the game", winner);
      connections.sendMessage(command.getAuthToken(), new Notification(ServerMessage.ServerMessageType.NOTIFICATION, observe));
      return;
    }

    try {
      game.makeMove(command.getMove());
    } catch (InvalidMoveException e) {
      String errorM="your move is invalid";
      connections.sendMessage(command.getAuthToken(), new Error(ServerMessage.ServerMessageType.ERROR, errorM));
      return;
    }
    String name=service.getDB().getSession(command.getAuthToken()).username();
    String note=String.format("%s made a move", name);

    var notification=new Notification(ServerMessage.ServerMessageType.NOTIFICATION, note);
    connections.broadcast(command.getAuthToken(), notification);
    var move=new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
    connections.broadcast(command.getAuthToken(), move);
    
    service.getDB().removeGame(chessGame);
    service.getDB().reAddGame(new GameData(chessGame.gameID(), chessGame.whiteUsername(), chessGame.blackUsername(), chessGame.gameName(), game));
if (game.isInCheckmate(game.getTeamTurn())){
  String winn = "";
  if (game.getTeamTurn() == ChessGame.TeamColor.BLACK){
    winn =chessGame.blackUsername();
    service.getDB().addWinner(chessGame, chessGame.blackUsername());
  }
  else{
    winn =chessGame.whiteUsername();
    service.getDB().addWinner(chessGame, chessGame.blackUsername());
  }
  Notification winMess = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has won the game", winn));
  connections.broadcast(command.getAuthToken(), winMess);
  connections.sendMessage(command.getAuthToken(), winMess);
  return;
}

    if (game.isInCheck(game.getTeamTurn())) {
      String mess=String.format("%s is in check", game.getTeamTurn().toString());
      ServerMessage notif = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mess);
      connections.broadcast(command.getAuthToken(), notif);
      connections.sendMessage(command.getAuthToken(), notif);
    }
  }


}
