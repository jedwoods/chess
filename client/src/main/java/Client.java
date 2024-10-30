import com.google.gson.Gson;

import java.util.Arrays;

public class Client {
  ServerFacade server;
  State state;



  public Client(String site) {
    this.server = new ServerFacade(site);
    this.state = State.SIGNEDOUT;
  }

  public String help() {
    if (state == State.SIGNEDOUT) {
      return """
                    - signIn <yourname>
                    - quit - get outta here
                    - help
                    - register <name> <password> <email>
                    """;
    }
    return """
                - list - gets all games
                - create game <>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
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
        case "sign in" -> login(params);
        case "list" -> listGames();
        case "play game" -> joinGame(params);
        case "observe game" -> observe();
        case "quit" -> "quit";
        default -> help();
      };
    } catch (ResponseException ex) {
      return ex.getMessage();
    }
  }

  public String listGames() throws ResponseException {
    assertSignedIn();
    var games = server.listGames();
    var result = new StringBuilder();
    var gson = new Gson();
    for (var game : games) {
      result.append(gson.toJson(game)).append('\n');
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


  public String login(String[] params) throws ResponseException {
    if (params.length == 2) {
      ResponseObject response = server.login(params[0], params[0]);
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



}
