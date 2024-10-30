import static ui.EscapeSequences.*;

public class ServerFacade {


  public ServerFacade(String site) {

  }

  public String help(){
    return "";
  }


  public String eval(String line) {
    try {
      var tokens = input.toLowerCase().split(" ");
      var cmd = (tokens.length > 0) ? tokens[0] : "help";
      var params = Arrays.copyOfRange(tokens, 1, tokens.length);
      return switch (cmd) {
        case "signin" -> signIn(params);
        case "rescue" -> rescuePet(params);
        case "list" -> listPets();
        case "signout" -> signOut();
        case "adopt" -> adoptPet(params);
        case "adoptall" -> adoptAllPets();
        case "quit" -> "quit";
        default -> help();
      };
    } catch ( ex) {
      return ex.getMessage();
    }

  }
}
