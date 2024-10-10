package dataaccess;



import chess.ChessGame;
import dataaccess.GameDataBase.*;
import dataaccess.UserDataBase.*;
import dataaccess.authDataBase.*;

public class DataAccess {
  authDataBase sessions;
  GameDataBase games;
  userDataBase users;
  int gameNum;

  public DataAccess(){
    this.sessions = new authDataBase();
    this.games = new GameDataBase();
    this.users = new userDataBase();
    this.gameNum = 0;
  }

  public void clear(){
    sessions = new authDataBase();
    games = new GameDataBase();
    users = new userDataBase();
  }


  public user userCheck(String userName){
    return users.get(userName);
  }

  public void addUser(user newUser){
    users.add(newUser);
  }


  public authToken makeToken(String userName){
    return sessions.newSession(userName);
  }

  public void addToken(authToken token){
    sessions.add(token);
  }

  public boolean confirmSession(String token){
    return sessions.get(token) != null;
  }

  public gameData addGame(String gameName){
    gameData game = new gameData(gameNum, null, null, gameName, new ChessGame());
    games.add(game);
    return game;
  }

public boolean isEmpty(){
    return games.size() == 0 && users.size() == 0 && sessions.size() == 0;
}

}
