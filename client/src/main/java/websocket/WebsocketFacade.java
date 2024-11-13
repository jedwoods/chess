package websocket;



import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.Gson;
import records.ResponseException;
import ui.ServerObserver;
import websocket.commands.JoinGameCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


import javax.print.DocFlavor;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.WebSocket;
import java.util.Locale;


public class WebsocketFacade extends Endpoint {
  ServerObserver notificationHandler;
  Session session;

  public WebsocketFacade(String url, ServerObserver notificationHandler) throws ResponseException {
    try {
      url = url.replace("http", "ws");
      URI socketURI = new URI(url + "/ws");
      this.notificationHandler = notificationHandler;

      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      this.session=container.connectToServer(this, socketURI);

      session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
          notificationHandler.notify(message);
        }
      });
    } catch (DeploymentException | IOException | URISyntaxException ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
  }


  public void playGame(String color, String authToken, int gameID) throws ResponseException {
    ChessGame.TeamColor passColor;
    if (color.equalsIgnoreCase("white")) {
      passColor = ChessGame.TeamColor.WHITE;
    }
    else {
      passColor=ChessGame.TeamColor.BLACK;
    }

    try {
      JoinGameCommand action;
      if (color.isEmpty()){
        action = new JoinGameCommand(UserGameCommand.CommandType.CONNECT, authToken,gameID, null );
      }
      else {
        action=new JoinGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, passColor);
      }
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
    } catch (IOException ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  public void leaveGame(String authToken, int gameID) throws ResponseException {
    try{
      var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
    } catch (IOException e) {
      throw new ResponseException(500, e.getMessage());
    }

  }

  public void resign(String authToken, int gameID) {
    UserGameCommand action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID)


  }

  public void makeMove(ChessPosition start, ChessPosition end, String promotion) {
  }

}





