package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.userdatabase.User;
import facade.ServerFacade;
import records.GameData;
import records.ResponseException;
import records.ResponseObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class Client {
  ServerFacade server;
  State state;
  HashSet<GameData> games;
  List<String> letters;
  String username;
  GameState playing;
  ServerObserver observer;



  public Client(String site, ServerObserver observer) {
    this.server = new ServerFacade(site);
    this.state = State.SIGNEDOUT;
    this.letters = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");
    this.playing = GameState.NOTPLAYING;
    this.observer = observer;
  }


  public String help() {
    if (this.playing == GameState.PLAYING){
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
      var tokens = input.toLowerCase().split(" ");
      var cmd = (tokens.length > 0) ? tokens[0] : "help";
      var params = Arrays.copyOfRange(tokens, 1, tokens.length);
      return switch (cmd) {
        case "logout" -> logout();
        case "login" -> login(params);
        case "list" -> listGames();
        case "playgame" -> joinGame(params);
        case "create" -> newGame(params);
        case "observe" -> observe(params);
        case "register" -> register(params);
        case "redraw" -> redraw(params);
        case "leave" -> leave(params);
        case "move" -> makeMove(params);
        case "resign" -> resign(params);
        case "highlightmoves" -> listMoves(params);
        case "quit" -> "quit";
        default -> help();
      };
    } catch (ResponseException ex) {
      return ex.getMessage();
    }
  }

  private String newGame(String[] params) throws ResponseException {
    assertSignedIn();
    if (params.length == 1) {
      ResponseObject returned = server.createGame(params[0]);
      return returned.message();
    }
    else{
      throw new ResponseException(400, "Expected: <gameName>");
    }
  }

  public String listGames() throws ResponseException {
    assertSignedIn();
    this.games = server.listGames();
    var result = new StringBuilder();
    var gson = new Gson();
    int i = 1;
    if (games.isEmpty()){
      System.out.println("No games available");
    }
    for (var game : this.games) {
      result.append(String.format("%d. %s%n", i, game.gameName()));

      result.append("WHITE PLAYER: ");
      if (Objects.isNull(game.whiteUsername())){
        result.append("None");
      }else{
        result.append(game.whiteUsername());
      }
      result.append(" BLACK PLAYER: ");
      if (Objects.isNull(game.blackUsername())){
        result.append("None");
      }else{
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
      int i = 1;
      for (var game : this.games){
        if (i == id){
          if (params[1].strip().equalsIgnoreCase("WHITE")){
            if (username.equals(game.whiteUsername())){
              state = State.PLAYING;
              return printBoard(game.game());
            }
          }
          if (params[1].toUpperCase().strip().equals("BLACK")){
            if (username.equals(game.blackUsername())){
              return printBoard(game.game());
            }
          }
          ResponseObject response = server.joinGame(params[1].toUpperCase(), game.gameID());
          return printBoard(game.game());
        }
        i++;
      }
      throw new ResponseException(400, "invalid ID");
    } else{
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
      int i = 1;
      for (var game : this.games){
        if (i == id){
//          records.ResponseObject response = server.joinGame(null, game.gameID());
          return printBoard(game.game());
        }
        i++;
      }
      throw new ResponseException(400, "invalid ID");
    } else{
      throw new ResponseException(400, "Expected: <your name> <password>");
    }
  }


  public String getPiece(ChessPiece piece){
    if (piece == null){
      return "   ";
    } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
      return getString(piece, BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP);
    }else{
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


  public String printBoard(ChessGame game) {
    ChessBoard board = game.getBoard();
    var result = new StringBuilder();

    appendColumnLabels(result);

    for (int row = 8; row >= 1; row--) {
      reverseRow(result, row, board);
    }


    appendColumnLabels(result);

    result.append("\n\n");

    appendReverseLabels(result);

    for (int row = 1; row <= 8; row++) {
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



  public String logout() throws  ResponseException{
    assertSignedIn();
    try {
      server.logout();
      state = State.SIGNEDOUT;
      return "You have been logged out";
    } catch (Exception e){
      throw new ResponseException(500, "Failed to logout");
    }
  }



  public String register(String[] params) throws ResponseException {
    if (params.length == 3) {
      ResponseObject response= server.register(params[0], params[1], params[2]);
      username = params[0];
      state=State.SIGNEDIN;
      return "";
    } else {
      throw new ResponseException(400, "Expected: <your name> <password> <email>");
    }
  }
  public String login(String[] params) throws ResponseException {
    if (params.length == 2) {
      ResponseObject response = server.login(params[0], params[1]);
      state = State.SIGNEDIN;
      username = params[0];
      return "";
    } else{
      throw new ResponseException(400, "Expected: <your name> <password> <email>");
    }

  }

  private void assertSignedIn() throws ResponseException {
    if (state == State.SIGNEDOUT) {
      throw new ResponseException(400, "You must sign in");
    }
  }



  public void boardBuilder(StringBuilder result, int row, ChessBoard board){
//      result.append(" ").append(row).append(" ");

      for (int col = 8; col >= 1; col--) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));

        if ((row + col) % 2 == 0) {
          result.append(SET_BG_COLOR_LIGHT_GREY);
        } else {
          result.append(SET_BG_COLOR_DARK_GREEN);
        }

        result.append(" ").append(getPiece(piece)).append(" ");
      }
      result.append(RESET_BG_COLOR).append(" ");
  }


  public void reversedBuilder(StringBuilder result, int row, ChessBoard board){
//      result.append(" ").append(row).append(" ");

    for (int col = 1; col <= 8; col++) {
      ChessPiece piece = board.getPiece(new ChessPosition(row, col));

      if ((row + col) % 2 == 0) {

        result.append(SET_BG_COLOR_DARK_GREEN);
      } else {
        result.append(SET_BG_COLOR_LIGHT_GREY);
      }

      result.append(" ").append(getPiece(piece)).append(" ");
    }
    result.append(RESET_BG_COLOR).append(" ");
  }


}
