package dataaccess;



import chess.ChessGame;
import dataaccess.GameDataBase.*;
import dataaccess.UserDataBase.*;
import dataaccess.authDataBase.*;

public class Service {
  authDataBase sessions;
  GameDataBase games;
  userDataBase users;
  int gameNum;

  public Service(){
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


  public authToken register(user newuser) throws DataAccessException {
    user usercheck = users.get(newuser.username());
    if (usercheck != null){
      return null;
    }
    users.add(newuser);
    authToken newToken = sessions.newSession(newuser.username());
    sessions.add(newToken);

    return newToken;
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
