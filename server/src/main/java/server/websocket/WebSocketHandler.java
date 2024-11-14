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
      Error error = new Error(ServerMessage.ServerMessageType.ERROR, "invalid game authtoken");
      session.getRemote().sendString(error.toString());
      return;
    }
    if (game== null){

      Error error = new Error(ServerMessage.ServerMessageType.ERROR, "invalid game id");
      session.getRemote().sendString(error.toString());
      return;
    }
    connections.add(command.getAuthToken(), session, command.getGameID());
    switch (command.getCommandType()) {
      case RESIGN -> resign(command.getAuthToken(), command.getGameID(), command);
      case CONNECT -> connect(message, session);
      case LEAVE -> leave(command.getAuthToken(), command);
      case MAKE_MOVE -> makeMove(message);
    }
  }




  //  @OnWebSocketConnect
  public void connect(String message, Session session) throws IOException {
    JoinGameCommand command = new Gson().fromJson(message, JoinGameCommand.class);
    connections.add(command.getAuthToken(), session, command.getGameID());
    String name = service.getDB().getSession(command.getAuthToken()).username();
    ChessGame game = service.getDB().getGame(command.getGameID()).game();
    if (command.getColor() == null) {
      String messageReturn=String.format("%s has joined your game", name);
      var obs = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
      connections.sendMessage(command.getAuthToken(), obs, command.getGameID());

      var notification=new Notification(ServerMessage.ServerMessageType.NOTIFICATION, messageReturn);
      connections.broadcast(command.getAuthToken(), notification, command.getGameID());

    }else{
      var obs = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
      connections.sendMessage(command.getAuthToken(), obs, command.getGameID());
      String messageReturn=String.format("%s has joined your game as %s", name, command.getColor());
      var notification=new Notification(ServerMessage.ServerMessageType.NOTIFICATION, messageReturn);
      connections.broadcast(command.getAuthToken(), notification, command.getGameID());

    }

  }

  public void leave(String authToken, UserGameCommand command) throws IOException {
    connections.remove(authToken, command.getGameID());
    var gameID = command.getGameID();
    String username = this.service.getDB().getSession(command.getAuthToken()).username();
    GameData game = service.getDB().getGame(gameID);
    ChessGame chessGame = game.game();
    String name = service.getDB().getSession(authToken).username();
    String message = String.format("%s has left your game", name);
    var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);

    if (Objects.equals(game.blackUsername(), username)){
      game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
    }else if (Objects.equals(game.whiteUsername(), username)){
      game = new GameData(game.gameID(),null, game.blackUsername(), game.gameName(), game.game());
    }
    service.getDB().reAddGame(game);
    var sender = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "you have left the game");
    connections.sendMessage(authToken,sender, command.getGameID());
    connections.remove(authToken, command.getGameID());
    connections.broadcast(authToken, notification, command.getGameID());
  }

  public void resign(String authToken,int gameID, UserGameCommand command) throws IOException {
    String name = service.getDB().getSession(authToken).username();
    GameData chessGame = service.getDB().getGame(gameID);
    ChessGame game =chessGame.game();
    String username = service.getDB().getSession(command.getAuthToken()).username();
    GameData data = service.getDB().getGame(gameID);
    String winner = service.getDB().getWinner(gameID);
    if (winner != null){
      var error = new Error(ServerMessage.ServerMessageType.ERROR, "game is already over");
      connections.sendMessage(command.getAuthToken(), error, command.getGameID());
      return;
    }
    if (!Objects.equals(name, data.blackUsername()) && !Objects.equals(username, data.whiteUsername())){
      Error error = new Error(ServerMessage.ServerMessageType.ERROR,"observers can't resign");
      connections.sendMessage(command.getAuthToken(), error, command.getGameID());
      return;
    }


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
      connections.broadcast(authToken, winMess, command.getGameID());
      connections.sendMessage(authToken, winMess, command.getGameID());
  }


  private void makeMove(String message) throws IOException {
    MakeMoveCommand command=new Gson().fromJson(message, MakeMoveCommand.class);
    ChessGame game=service.getDB().getGame(command.getGameID()).game();
    GameData chessGame = service.getDB().getGame(command.getGameID());
    String turn = service.getDB().getTurn(chessGame);
    String username = this.service.getDB().getSession(command.getAuthToken()).username();
    assert turn != null;
    if (turn.equalsIgnoreCase("black")){
      game.setTeamTurn(ChessGame.TeamColor.BLACK);
    }
    String winner = service.getDB().getWinner(chessGame.gameID());
    if (!Objects.equals(winner, null)){
      String observe = String.format("%s has already won the game", winner);
      connections.sendMessage(command.getAuthToken(), new Error(ServerMessage.ServerMessageType.ERROR, observe), command.getGameID());
      return;
    }

    String yourColor = null;
    if (Objects.equals(chessGame.blackUsername(), username)){
      yourColor = "black";
    }
    else if (Objects.equals(chessGame.whiteUsername(), username)){
      yourColor = "white";
    }
    if (yourColor == null){
      connections.sendMessage(command.getAuthToken(), new Error(ServerMessage.ServerMessageType.ERROR, "observer core"), command.getGameID());
      return;
    }
    if (!yourColor.equals(turn)){
      var error = new Error(ServerMessage.ServerMessageType.ERROR, "observer core");
      connections.sendMessage(command.getAuthToken(), error, command.getGameID());
      return;
    }


    try {
      game.makeMove(command.getMove());
      if (turn.equals("black")){
        service.getDB().setTurn(chessGame, "white");
      }
      else{
        service.getDB().setTurn(chessGame, "black");
      }
    } catch (InvalidMoveException e) {
      var start = command.getMove().getStartPosition();
      var end = command.getMove().getEndPosition();
      String errorM=String.format("%s is invalid", convertCoords(start.getColumn(), start.getColumn(), end.getColumn(), end.getRow()));
      connections.sendMessage(command.getAuthToken(), new Error(ServerMessage.ServerMessageType.ERROR, errorM), command.getGameID());
      return;
    }
    var start = command.getMove().getStartPosition();
    var end = command.getMove().getEndPosition();
    String name=service.getDB().getSession(command.getAuthToken()).username();
    String note=String.format("%s moved %s", name, convertCoords(start.getColumn(), start.getColumn(), end.getColumn(), end.getRow()));

    var notification=new Notification(ServerMessage.ServerMessageType.NOTIFICATION, note);
    connections.broadcast(command.getAuthToken(), notification, command.getGameID());
    var move=new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
    connections.broadcast(command.getAuthToken(), move, command.getGameID());
    connections.sendMessage(command.getAuthToken(), move, command.getGameID());

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
  connections.broadcast(command.getAuthToken(), winMess, command.getGameID());
  connections.sendMessage(command.getAuthToken(), winMess, command.getGameID());
  return;
}
    if (game.isInCheck(game.getTeamTurn())) {
      String mess=String.format("%s is in check", game.getTeamTurn().toString());
      ServerMessage notif = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mess);
      connections.broadcast(command.getAuthToken(), notif, command.getGameID());
      connections.sendMessage(command.getAuthToken(), notif, command.getGameID());
    }
  }

  public String convertCoords(int col1, int row1, int col2, int row2) {

    char columnLetter1 = (char) ('a' + col1 - 1); // '1' becomes 'a', '2' becomes 'b', etc.
    char columnLetter2 = (char) ('a' + col2 - 1);

    String coord1 = "" + columnLetter1 + row1;
    String coord2 = "" + columnLetter2 + row2;

    return coord1 + " " + coord2;
  }

}
