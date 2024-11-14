package ui;
import chess.*;
import com.google.gson.Gson;
import facade.ServerFacade;
import records.GameData;
import records.ResponseException;
import records.ResponseObject;
import websocket.WebsocketFacade;

import java.util.*;

import static ui.EscapeSequences.*;

public class Client {
  ServerFacade server;
  State state;
  HashSet<GameData> games;
  List<String> letters;
  String username;
  GameState playing;
  ServerObserver observer;
  WebsocketFacade ws;
  String site;
  int gameID;
  ChessGame.TeamColor color;


  public Client(String site, ServerObserver observer) {
    this.server=new ServerFacade(site);
    this.state=State.SIGNEDOUT;
    this.letters=Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");
    this.playing=GameState.NOTPLAYING;
    this.observer=observer;
    this.site=site;

  }


  public String help() {
    if (this.playing == GameState.PLAYING) {
      return """
              - Help
              - Redraw
              - Leave
              - Move
              - Resign
              - HighlightMoves
              """;
    }
    if (state == State.SIGNEDOUT) {
      return """
              - login <username> <password>
              - quit - get outta here
              - help
              - register <name> <password> <email>
              """;
    }
    return """
            - list - gets all games
            - create <game name>
            - playGame <game ID> [white|black]
            - observe <game ID>
            - logout
            - quit
            """;
  }

  public String eval(String input) {
    try {
      var tokens=input.toLowerCase().split(" ");
      var cmd=(tokens.length > 0) ? tokens[0] : "help";
      var params=Arrays.copyOfRange(tokens, 1, tokens.length);
      return switch (cmd) {
        case "logout" -> logout();
        case "login" -> login(params);
        case "list" -> listGames();
        case "playgame" -> joinGame(params);
        case "create" -> newGame(params);
        case "observe" -> observe(params);
        case "register" -> register(params);
        case "redraw" -> redraw();
        case "leave" -> leave();
        case "move" -> makeMove(params);
        case "resign" -> resign();
        case "highlightmoves" -> listMoves(params);
        case "quit" -> "quit";
        default -> help();
      };
    } catch (ResponseException ex) {
      return ex.getMessage();
    }
  }

  private String makeMove(String[] params) throws ResponseException {
    assertSignedIn();
    if (playing != GameState.PLAYING) {
      return "you are not playing a game";
    }
    if (params.length != 2 && params.length != 3) {
      throw new ResponseException(500, "expected: makeMove <from> <to>");
    }
    ChessPosition start = assertCord(params[0]);
    ChessPosition end = assertCord(params[1]);
    String promotion = "null";
    if (params.length == 3){
      promotion = params[2];
    }

    try{
      ws.makeMove(start, end, promotion, server.getAuthToken(), this.gameID);
      return String.format("%s ", "invalid move");
    }catch (ResponseException e){
      throw new ResponseException(500, e.getMessage());
    }

  }

  private String resign() throws ResponseException {
    assertSignedIn();
    if (playing != GameState.PLAYING) {
      return "you are not playing a game";
    }
    System.out.print("Do you want to resign?\nY|N\n");
    Scanner scanner = new Scanner(System.in);
    String res = scanner.nextLine().strip();
    if (res.equalsIgnoreCase("y")){
      ws.resign(server.getAuthToken(), this.gameID);
    }
    return "";
  }


  private String redraw() throws ResponseException {
    assertSignedIn();
    if (playing != GameState.PLAYING) {
      return "you are not playing a game";
    }

    this.server.listGames();
    for (var c : this.games) {
      if (c.gameID() == this.gameID) {
        if (this.color == ChessGame.TeamColor.BLACK) {
          return printBlackBoard(c.game());
        }
        return printWhite(c.game());
      }
    }
    return "can't find your game brother, sorry";
  }

  private String newGame(String[] params) throws ResponseException {
    assertSignedIn();
    if (params.length == 1) {
      ResponseObject returned=server.createGame(params[0]);
      return returned.message();
    } else {
      throw new ResponseException(400, "Expected: <gameName>");
    }
  }

  public String listGames() throws ResponseException {
    assertSignedIn();
    this.games=server.listGames();
    var result=new StringBuilder();
    var gson=new Gson();
    int i=1;
    if (games.isEmpty()) {
      System.out.println("No games available");
    }
    for (var game : this.games) {
      result.append(String.format("%d. %s%n", i, game.gameName()));

      result.append("WHITE PLAYER: ");
      if (Objects.isNull(game.whiteUsername())) {
        result.append("None");
      } else {
        result.append(game.whiteUsername());
      }
      result.append(" BLACK PLAYER: ");
      if (Objects.isNull(game.blackUsername())) {
        result.append("None");
      } else {
        result.append(game.blackUsername());
      }
      result.append("\n");
      i++;
    }

    return result.toString();
  }

  public String joinGame(String[] params) throws ResponseException {
    assertSignedIn();
    int id;
    if (params.length == 2) {
//      records.ResponseObject response = server.joinGame(params[0], params[0]);
      try {
        id=Integer.parseInt(params[0]);
      } catch (NumberFormatException e) {
        throw new ResponseException(401, "input must be a valid index");
      }
      int i=1;
      for (var game : this.games) {
        if (i == id) {
          if (params[1].strip().equalsIgnoreCase("WHITE")) {
            this.color=ChessGame.TeamColor.WHITE;
            if (username.equals(game.whiteUsername())) {
              this.playing=GameState.PLAYING;
              this.gameID=game.gameID();
              ws=new WebsocketFacade(this.site, observer);
              ws.playGame(params[1], this.server.getAuthToken(), game.gameID());
              return printWhite(game.game());
            }
          }
          if (params[1].toUpperCase().strip().equals("BLACK")) {
            this.color=ChessGame.TeamColor.BLACK;
            if (username.equals(game.blackUsername())) {
              this.playing=GameState.PLAYING;
              ws=new WebsocketFacade(this.site, observer);
              ws.playGame(params[1], this.server.getAuthToken(), game.gameID());
              this.gameID=game.gameID();
              return printBlackBoard(game.game());
            }
          }
          ResponseObject response=server.joinGame(params[1].toUpperCase(), game.gameID());
          this.playing=GameState.PLAYING;
          ws=new WebsocketFacade(this.site, observer);
          ws.playGame(params[1], this.server.getAuthToken(), game.gameID());
          this.gameID=game.gameID();
          if (this.color == ChessGame.TeamColor.WHITE) {
            return printWhite(game.game());
          } else {
            return printBlackBoard(game.game());
          }
        }
        i++;
      }
      throw new ResponseException(400, "invalid ID");
    } else {
      throw new ResponseException(400, "Expected: playgame <gameID> <[BLACK|WHITE]>");
    }
  }

  public String observe(String[] params) throws ResponseException {
    assertSignedIn();
    int id;
    if (params.length == 1) {
//      records.ResponseObject response = server.joinGame(params[0], params[0]);
      try {
        id=Integer.parseInt(params[0]);
      } catch (NumberFormatException e) {
        throw new ResponseException(401, "input must be a valid index");
      }
      int i=1;
      for (var game : this.games) {
        if (i == id) {
//          records.ResponseObject response = server.joinGame(null, game.gameID());
          return printWhite(game.game());
        }
        i++;
      }
      throw new ResponseException(400, "invalid ID");
    } else {
      throw new ResponseException(400, "Expected: <your name> <password>");
    }
  }


  public String getPiece(ChessPiece piece) {
    if (piece == null) {
      return "   ";
    } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
      return getString(piece, BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP);
    } else {
      return getString(piece, WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP);
    }
  }

  private String getString(ChessPiece piece, String bP, String bR, String blackKnight, String blackKing, String blackQueen, String blackBishop) {
    return switch (piece.getPieceType()) {
      case ChessPiece.PieceType.PAWN -> bP;
      case ChessPiece.PieceType.ROOK -> bR;
      case ChessPiece.PieceType.KNIGHT -> blackKnight;
      case ChessPiece.PieceType.KING -> blackKing;
      case ChessPiece.PieceType.QUEEN -> blackQueen;
      case ChessPiece.PieceType.BISHOP -> blackBishop;
    };
  }


  public String printWhite(ChessGame game) {
    ChessBoard board=game.getBoard();
    var result=new StringBuilder();

    appendColumnLabels(result);

    for (int row=8; row >= 1; row--) {
      reverseRow(result, row, board);
    }


    appendColumnLabels(result);
    return result.toString();
  }

  public String printBlackBoard(ChessGame game) {
    ChessBoard board=game.getBoard();
    var result=new StringBuilder();
    appendReverseLabels(result);

    for (int row=1; row <= 8; row++) {
      appendRow(result, row, board);
    }
    appendReverseLabels(result);

    return result.toString();
  }


  private void appendColumnLabels(StringBuilder result) {
    result.append("   ");
    for (String c : letters) {
      result.append("  ").append(c).append("  ");
    }
    result.append("\n");
  }

  private void appendReverseLabels(StringBuilder result) {
    result.append("   ");
    for (String c : letters.reversed()) {
      result.append("  ").append(c).append("  ");
    }
    result.append("\n");
  }

  private void appendRow(StringBuilder result, int row, ChessBoard board) {
    result.append(" ").append(row).append(" "); // Row label
    boardBuilder(result, row, board); // Build row content
    result.append(RESET_BG_COLOR).append(" ").append(row).append("\n"); // Row end and reset colors
  }

  private void reverseRow(StringBuilder result, int row, ChessBoard board) {
    result.append(" ").append(row).append(" "); // Row label
    reversedBuilder(result, row, board); // Build row content
    result.append(RESET_BG_COLOR).append(" ").append(row).append("\n"); // Row end and reset colors
  }


  public String logout() throws ResponseException {
    assertSignedIn();
    try {
      if (this.playing == GameState.PLAYING){
        this.leave();
      }
      server.logout();
      state=State.SIGNEDOUT;

      return "You have been logged out";
    } catch (Exception e) {
      throw new ResponseException(500, "Failed to logout");
    }
  }


  public String register(String[] params) throws ResponseException {
    if (params.length == 3) {
      ResponseObject response=server.register(params[0], params[1], params[2]);
      username=params[0];
      state=State.SIGNEDIN;
      return "";
    } else {
      throw new ResponseException(400, "Expected: <your name> <password> <email>");
    }
  }

  public String login(String[] params) throws ResponseException {
    if (params.length == 2) {
      ResponseObject response=server.login(params[0], params[1]);
      state=State.SIGNEDIN;
      username=params[0];
      return "";
    } else {
      throw new ResponseException(400, "Expected: <your name> <password> <email>");
    }

  }

  private void assertSignedIn() throws ResponseException {
    if (state == State.SIGNEDOUT) {
      throw new ResponseException(400, "You must sign in");
    }
  }


  public void boardBuilder(StringBuilder result, int row, ChessBoard board) {
//      result.append(" ").append(row).append(" ");

    for (int col=8; col >= 1; col--) {
      ChessPiece piece=board.getPiece(new ChessPosition(row, col));

      if ((row + col) % 2 != 0) {
        result.append(SET_BG_COLOR_LIGHT_GREY);
      } else {
        result.append(SET_BG_COLOR_DARK_GREEN);
      }

      result.append(" ").append(getPiece(piece)).append(" ");
    }
    result.append(RESET_BG_COLOR).append(" ");
  }


  public void reversedBuilder(StringBuilder result, int row, ChessBoard board) {
//      result.append(" ").append(row).append(" ");

    for (int col=1; col <= 8; col++) {
      ChessPiece piece=board.getPiece(new ChessPosition(row, col));

      if ((row + col) % 2 == 0) {

        result.append(SET_BG_COLOR_DARK_GREEN);
      } else {
        result.append(SET_BG_COLOR_LIGHT_GREY);
      }

      result.append(" ").append(getPiece(piece)).append(" ");
    }
    result.append(RESET_BG_COLOR).append(" ");
  }

  public String leave() throws ResponseException {
    assertSignedIn();
    if (playing != GameState.PLAYING) {
      return "you are not playing a game";
    }
    ws.leaveGame(server.getAuthToken(), this.gameID);
    gameID=-1;
    ws=null;
    playing=GameState.NOTPLAYING;
    return "You have left the game";
  }

  private String listMoves(String[] params) throws ResponseException {
    if (params.length != 1) {
      throw new ResponseException(500, "expected: HighlightMoves <location>");
    }
    ChessPosition start = assertCord(params[0]);

    for (var c : this.games) {
      if (c.gameID() == this.gameID) {
        Collection<ChessMove> moves = c.game().validMoves(start);
//        System.out.println(this.getPiece(c.game().getBoard().getPiece(start)));
        if (this.color == ChessGame.TeamColor.BLACK) {

          return printValidBlack(start,moves, c.game());
        }
        else{
          return printValidWhite(start,moves, c.game());

        }
      }
    }
    throw new ResponseException(500, "could not validate game");
  }

  private String printValidBlack(ChessPosition start, Collection<ChessMove> moves, ChessGame game) {
    ChessBoard board=game.getBoard();
    var result=new StringBuilder();
    appendReverseLabels(result);

    for (int row=1; row <= 8; row++) {
      result.append(" ").append(row).append(" ");
      highlightBuilder(result, row, board, start, moves);
      result.append(RESET_BG_COLOR).append(" ").append(row).append("\n");
    }
    appendReverseLabels(result);

    return result.toString();
  }


  private String printValidWhite(ChessPosition start, Collection<ChessMove> moves, ChessGame game) {
    ChessBoard board=game.getBoard();
    var result=new StringBuilder();
    appendColumnLabels(result);
    for (int row=8; row >= 1; row--) {
      result.append(" ").append(row).append(" ");
      reverseHighlight(result, row, board, start, moves);
      result.append(RESET_BG_COLOR).append(" ").append(row).append("\n");
    }
    appendColumnLabels(result);
    return result.toString();
  }


  public void highlightBuilder(StringBuilder result, int row, ChessBoard board,ChessPosition start, Collection<ChessMove> moves ) {

    for (int col=8; col >= 1; col--) {
      reverseRow(result, row, board, start, moves, col);
    }
    result.append(RESET_BG_COLOR).append(" ");
  }


  public void reverseHighlight(StringBuilder result, int row, ChessBoard board,ChessPosition start, Collection<ChessMove> moves ) {

    for (int col=1; col <= 8; col++) {
      colorRow(result, row, board, start, moves, col);
    }
    result.append(RESET_BG_COLOR).append(" ");
  }



  private boolean validMove(int row, int col, Collection<ChessMove> moves, ChessPosition start, ChessBoard board){
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

  private void reverseRow(StringBuilder result, int row, ChessBoard board, ChessPosition start, Collection<ChessMove> moves, int col){

    ChessPiece piece=board.getPiece(new ChessPosition(row, col));
    boolean flag = validMove(row, col, moves, start, board);
    if (flag){
      if ((row + col) % 2 != 0) {
        result.append(SET_BG_COLOR_WHITE);
      } else {
        result.append(SET_BG_COLOR_GREEN);
      }
    }else{
      if ((row + col) % 2 != 0) {
        result.append(SET_BG_COLOR_LIGHT_GREY);
      } else {
        result.append(SET_BG_COLOR_DARK_GREEN);
      }
    }

    result.append(" ").append(getPiece(piece)).append(" ");
  }
  private void colorRow(StringBuilder result, int row, ChessBoard board, ChessPosition start, Collection<ChessMove> moves, int col) {
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


  public ChessPosition assertCord(String move) throws ResponseException {
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
}

