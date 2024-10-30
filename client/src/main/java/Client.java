import java.util.ArrayList;
import java.util.Arrays;

public class Client {



  public Client(String site) {


  }

  public String help(){
    return "";
  }


  public String eval(String input) {
    try {
      var tokens = input.toLowerCase().split(" ");
      var cmd = (tokens.length > 0) ? tokens[0] : "help";
      var params = Arrays.copyOfRange(tokens, 1, tokens.length);
      return switch (cmd) {
        case "help" -> help();
        case "logout" -> logout(params);
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


  public String login(ArrayList<String> params){
    if (params.size() >= 2) {


    } else{
      throw new ResponseException(400, "Expected <your name> <password>");
    }

  }





}
