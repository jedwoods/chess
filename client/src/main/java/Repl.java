import java.util.Scanner;

import static ui.EscapeSequences.*;


public class Repl {
  Client client;

  public Repl(String site){
    this.client = new Client(site);

  }


  public void run() {
    System.out.println("♘ Welcome to the Chess. Sign in to start.");
    System.out.print(client.help());

    Scanner scanner = new Scanner(System.in);
    var result = "";
    while (!result.equals("quit")) {
      printPrompt();
      String line = scanner.nextLine();

      try {
        result = client.eval(line);
        System.out.print(SET_TEXT_COLOR_BLUE + result);
      } catch (Throwable e) {
        var msg = e.toString();
        System.out.print(msg);
      }
    }
    System.out.println();
  }

  private void printPrompt() {
    System.out.print("\n" + RESET + "[" + client.state + "] >>> " + SET_TEXT_COLOR_GREEN);
  }

  public void notify(String notification) {
    System.out.println(SET_TEXT_COLOR_BLUE + notification);
    printPrompt();
  }


}




