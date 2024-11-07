package ui;

import com.google.gson.Gson;
import websocket.messages.Error;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;


public class Repl implements ServerObserver {
  Client client;

  public Repl(String site){
    this.client = new Client(site, this);
  }


  public void run() {
    System.out.println(SET_TEXT_COLOR_BLUE + "♘ Welcome to the Chess. Sign in or Register to start.");

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
    if (client.playing == GameState.PLAYING ) {
      System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_BLUE + "[" + client.playing + "] >>> " + SET_TEXT_COLOR_GREEN);
      return;
    }
    System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_BLUE + "[" + client.state + "] >>> " + SET_TEXT_COLOR_GREEN);
  }


  @Override
  public void notify(String message){
    ServerMessage note = new Gson().fromJson(message, ServerMessage.class);
    if (note.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
      System.out.println(new Gson().fromJson(message, Notification.class).getMesssage());
    }
    else if (note.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
      System.out.println(new Gson().fromJson(message, Error.class).getErrorMessage());
    }else if (note.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
      var game = new Gson().fromJson(message, LoadGameMessage.class).getGame();
      System.out.println(this.client.printWhite(game));
    }else {
      System.out.println(message);
    }
    printPrompt();
  }
}
