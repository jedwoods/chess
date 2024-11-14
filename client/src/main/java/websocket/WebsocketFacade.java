package websocket;



import chess.*;
import com.google.gson.Gson;
import records.ResponseException;
import ui.ServerObserver;
import websocket.commands.JoinGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


import javax.print.DocFlavor;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.WebSocket;
import java.util.Collection;
import java.util.Locale;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREEN;


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

  public void resign(String authToken, int gameID) throws ResponseException {
    UserGameCommand action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
    try {
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
    } catch (IOException e) {
      throw new ResponseException(500, e.getMessage());
    }

  }
  public void makeMove(ChessPosition s,ChessPosition e,String promotion,String t,int gameID,ChessGame.TeamColor c)throws ResponseException{
    ChessPiece.PieceType prom = pieceMap(promotion);
    ChessMove move = new ChessMove(s,e,prom);
    MakeMoveCommand action = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, t, gameID, move, c);
    try {
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
    } catch (IOException ex) {
      throw new ResponseException(500, ex.getMessage());
    }


  }

  private ChessPiece.PieceType pieceMap(String promotion) throws ResponseException {
    switch (promotion.toLowerCase()){
      case "pawn" -> {
        return ChessPiece.PieceType.PAWN;
      }
      case "rook" ->{
        return ChessPiece.PieceType.ROOK;
      }
      case "knight" ->{
        return ChessPiece.PieceType.KNIGHT;
      }case "bishop" ->{
        return ChessPiece.PieceType.BISHOP;
      }case "queen" ->{
        return ChessPiece.PieceType.QUEEN;
      }case "null" ->{
        return null;
      }
      default -> throw new IllegalStateException("Unexpected value: " + promotion.toLowerCase());
    }

  }


  public static ChessPosition assertCord(String move) throws ResponseException {
    if (move.length() != 2){
      throw new ResponseException(500, "expect: <row><col>");
    }
    int col = move.charAt(0) - 'a' + 1;
    int row = Character.getNumericValue(move.charAt(1));
    if (row < 0){
      row *= -1;
    }
    if(col >= 1 && col <= 8 && row >= 1 && row <= 8){
      return new ChessPosition( row, col);
    }
    throw new ResponseException(500, "position out of board");
  }

  public static String getString(ChessPiece piece, String bP, String bR, String blackKnight, String blackKing, String blackQueen, String blackBishop){
    return switch (piece.getPieceType()) {
      case ChessPiece.PieceType.PAWN -> bP;
      case ChessPiece.PieceType.ROOK -> bR;
      case ChessPiece.PieceType.KNIGHT -> blackKnight;
      case ChessPiece.PieceType.KING -> blackKing;
      case ChessPiece.PieceType.QUEEN -> blackQueen;
      case ChessPiece.PieceType.BISHOP -> blackBishop;
    };
  }

  public static void colorRow(StringBuilder result, int row, ChessBoard board, ChessPosition start, Collection<ChessMove> moves, int col) {
    ChessPiece piece=board.getPiece(new ChessPosition(row, col));
    boolean flag = validMove(row, col, moves, start, board);
    if (flag){
      if ((row + col) % 2 == 0) {
        result.append(SET_BG_COLOR_WHITE);
      } else {
        result.append(SET_BG_COLOR_GREEN);
      }
    }else{
      if ((row + col) % 2 == 0) {
        result.append(SET_BG_COLOR_LIGHT_GREY);
      } else {
        result.append(SET_BG_COLOR_DARK_GREEN);
      }
    }
    result.append(" ").append(getPiece(piece)).append(" ");
  }

  public static boolean validMove(int row, int col, Collection<ChessMove> moves, ChessPosition start, ChessBoard board){
    ChessPosition end = new ChessPosition(row, col);

    for (ChessMove move : moves){
      int first = move.getStartPosition().getColumn();
      int second = move.getStartPosition().getRow();
      int third = move.getEndPosition().getRow();
      int fourth = move.getEndPosition().getColumn();
      if (first==start.getColumn() && second == start.getRow() && third == end.getRow() && fourth == end.getColumn() ){
        return true;

      }
    }
    return false;
  }

  public static String getPiece(ChessPiece piece) {
    if (piece == null) {
      return "   ";
    } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
      return getString(piece, BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP);
    } else {
      return getString(piece, WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP);
    }
  }
}





