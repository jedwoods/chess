
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.gamedatabase.GameData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static ui.EscapeSequences.*;

public class Client {
  ServerFacade server;
  State state;
  HashSet<GameData> games;
  List<String> letters;



  public Client(String site) {
    this.server = new ServerFacade(site);
    this.state = State.SIGNEDOUT;
    this.letters = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");
  }

  public String help() {
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
                - observe game <game ID>
                - signOut
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
//        case "observegame" -> observe();
        case "register" -> register(params);
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
      result.append(String.format("%d. %s%n", i, game.gameName())).append("\n");
      i++;
    }

    return result.toString();
  }

  public String joinGame(String[] params) throws ResponseException {
    assertSignedIn();
    if (params.length == 2) {
//      ResponseObject response = server.joinGame(params[0], params[0]);
      int id =  Integer.parseInt(params[0]);
      int i = 1;
      for (var game : this.games){
        if (i == id){
          ResponseObject response = server.joinGame(params[1].toUpperCase(), game.gameID());
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
      return switch (piece.getPieceType()) {
        case ChessPiece.PieceType.PAWN -> BLACK_PAWN;
        case ChessPiece.PieceType.ROOK -> BLACK_ROOK;
        case ChessPiece.PieceType.KNIGHT -> BLACK_KNIGHT;
        case ChessPiece.PieceType.KING -> BLACK_KING;
        case ChessPiece.PieceType.QUEEN -> BLACK_QUEEN;
        case ChessPiece.PieceType.BISHOP -> BLACK_BISHOP;
      };
    }else{
      return switch (piece.getPieceType()) {
        case ChessPiece.PieceType.PAWN -> WHITE_PAWN;
        case ChessPiece.PieceType.ROOK -> WHITE_ROOK;
        case ChessPiece.PieceType.KNIGHT -> WHITE_KNIGHT;
        case ChessPiece.PieceType.KING -> WHITE_KING;
        case ChessPiece.PieceType.QUEEN -> WHITE_QUEEN;
        case ChessPiece.PieceType.BISHOP -> WHITE_BISHOP;
      };
    }
  }


  public String printBoard(ChessGame game) {
    List<String> letters = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");
    ChessBoard board = game.getBoard();
    var result = new StringBuilder();

    // Top border with column labels
    result.append("   ");
    for (String c : letters) {
      result.append("  ").append(c).append("  ");
    }
    result.append("\n");

    // Print each row
    for (int row = 8; row >= 1; row--) { // Rows go from 8 to 1 in chess
      result.append(" ").append(row).append(" "); // Row label
      boardBuilder(result, row, board);
      result.append(RESET_BG_COLOR).append(" ").append(row).append("\n"); // Row end and reset colors
    }

    // Bottom border with column labels
    result.append("   ");
    for (String c : letters) {
      result.append(" ").append(c).append(" ");
    }

    return result.toString();
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
      state=State.SIGNEDIN;
      return "";
    } else {
      throw new ResponseException(400, "Expected: <your name> <password>");
    }
  }
  public String login(String[] params) throws ResponseException {
    if (params.length == 2) {
      ResponseObject response = server.login(params[0], params[1]);
      state = State.SIGNEDIN;
      return "";
    } else{
      throw new ResponseException(400, "Expected: <your name> <password>");
    }

  }

  private void assertSignedIn() throws ResponseException {
    if (state == State.SIGNEDOUT) {
      throw new ResponseException(400, "You must sign in");
    }
  }

  public String printBackwards(ChessGame game) {
    ChessBoard board=game.getBoard();
    var result=new StringBuilder();

    result.append("   ");
    for (String c : letters) {
      result.append("  ").append(c).append("  ");
    }
    result.append("\n");
    for (int row=1; row <= 8; row++) { // Rows go from 8 to 1 in chess
      boardBuilder(result, row, board);
      // Bottom border with column labels
      result.append("   ");
      for (String c : letters) {
        result.append(" ").append(c).append(" ");
      }
    }
    return result.toString();
  }

  public void boardBuilder(StringBuilder result, int row, ChessBoard board){
      result.append(" ").append(row).append(" "); // Row label

      for (int col = 1; col <= 8; col++) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));

        // Alternate colors for checkerboard pattern
        if ((row + col) % 2 == 0) {
          result.append(SET_BG_COLOR_LIGHT_GREY);
        } else {
          result.append(SET_BG_COLOR_DARK_GREEN);
        }

        result.append(" ").append(getPiece(piece)).append(" ");
      }
      result.append(RESET_BG_COLOR).append(" ").append(row).append("\n"); // Row end and reset colors
  }


}
