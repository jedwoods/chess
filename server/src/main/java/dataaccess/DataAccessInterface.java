package dataaccess;

import chess.ChessGame;
import dataaccess.authdatabase.AuthDataBase;
import dataaccess.authdatabase.AuthToken;
import dataaccess.gamedatabase.GameData;
import dataaccess.gamedatabase.GameDataBase;
import dataaccess.userdatabase.User;
import dataaccess.userdatabase.UserDataBase;

import java.util.ArrayList;

public interface DataAccessInterface {
  public void clear() throws DataAccessException;

  public User userCheck(String userName);

  public void addUser(User newUser);

  public User getUser(String username);

  public AuthToken makeToken(String userName);

  public void addToken(AuthToken token);

  public boolean confirmSession(String token);

  public GameData addGame(String gameName);

  public void reAddGame(GameData game);

  public void removeGame(GameData game);

  public void logout(String token);

  public ArrayList<GameData> listGames();

  public GameData getGame(int gameID);

  public AuthToken getSession(String token);



  public boolean isEmpty();


}
