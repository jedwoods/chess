package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;

public class MakeMoveCommand extends UserGameCommand{
  ChessMove move;
  ChessGame.TeamColor color;


  public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, ChessGame.TeamColor color) {
    super(commandType, authToken, gameID);
    this.move = move;
    this.color = color;
  }

  public ChessMove getMove(){
    return this.move;
  }

  public ChessGame.TeamColor getColor(){
    return this.color;
  }
}
