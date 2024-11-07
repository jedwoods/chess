package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;

public class LoadGameMessage extends ServerMessage{
  private final ChessGame game;

  public LoadGameMessage(ServerMessageType type, ChessGame game) {
    super(type);
    this.game=game;
  }

  public ChessGame getGame(){
    return this.game;
  }
}
