package dataAccess;



import chess.ChessGame;
import dataAccess.userDataBase.*;
import dataAccess.gameDataBase.*;
import dataAccess.authDataBase.*;

import java.util.ArrayList;

public class DataAccess {
  AuthDataBase sessions;
  GameDataBase games;
  UserDataBase users;
  int gameNum;

  public DataAccess(){
    this.sessions = new AuthDataBase();
    this.games = new GameDataBase();
    this.users = new UserDataBase();
    this.gameNum = 1;
  }

  public void clear(){
    sessions = new AuthDataBase();
    games = new GameDataBase();
    users = new UserDataBase();
  }


  public User userCheck(String userName){
    return users.get(userName);
  }

  public void addUser(User newUser){
    users.add(newUser);
  }

  public User getUser(String username){
    return users.get(username);
  }

  public AuthToken makeToken(String userName){
    return sessions.newSession(userName);
  }

  public void addToken(AuthToken token){
    sessions.add(token);
  }

  public boolean confirmSession(String token){
    return sessions.get(token) != null;
  }

  public GameData addGame(String gameName){
    GameData game = new GameData(gameNum, null, null, gameName, new ChessGame());
    games.add(game);
    gameNum += 1;
    return game;
  }

  public void reAddGame(GameData game){
    games.add(game);
  }

  public void removeGame(GameData game){
    games.remove(game.gameID());
  }

  public void logout(String token){
//    authToken currentToken = sessions.get(token);
    sessions.remove(token);
  }

  public ArrayList<GameData> listGames(){
    return games.listGames();
  }

  public GameData getGame(int gameID){
    return games.get(gameID);
  }

  public AuthToken getSession(String token){
    return sessions.get(token);

  }

public boolean isEmpty(){
    return games.size() == 0 && users.size() == 0 && sessions.size() == 0;
}

}
