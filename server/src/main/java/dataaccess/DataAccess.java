package dataaccess;



import chess.ChessGame;
import dataaccess.GameDataBase.*;
import dataaccess.UserDataBase.*;
import dataaccess.authDataBase.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class DataAccess {
  authDataBase sessions;
  GameDataBase games;
  userDataBase users;
  int gameNum;

  public DataAccess(){
    this.sessions = new authDataBase();
    this.games = new GameDataBase();
    this.users = new userDataBase();
    this.gameNum = 1;
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

  public user getUser(String username){
    return users.get(username);
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
    gameNum += 1;
    return game;
  }

  public void reAddGame(gameData game){
    games.add(game);
  }

  public void removeGame(gameData game){
    games.remove(game.gameID());
  }

  public void logout(String token){
//    authToken currentToken = sessions.get(token);
    sessions.remove(token);
  }

  public ArrayList<gameData> listGames(){
    return games.listGames();
  }

  public gameData getGame(int gameID){
    return games.get(gameID);
  }

  public authToken getSession(String token){
    return sessions.get(token);

  }

public boolean isEmpty(){
    return games.size() == 0 && users.size() == 0 && sessions.size() == 0;
}

}
