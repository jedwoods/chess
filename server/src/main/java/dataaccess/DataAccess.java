package dataaccess;



import chess.ChessGame;
import dataaccess.userdatabase.*;
import dataaccess.gamedatabase.*;
import dataaccess.authdatabase.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class DataAccess implements DataAccessInterface{
//  AuthDataBase sessions;
//  GameDataBase games;
//  UserDataBase users;
  SQLUserData users;
  SQLGamaData games;
  SQLAuthData sessions;
  int gameNum;

  public DataAccess(){

    this.sessions = new SQLAuthData();
    this.games = new SQLGamaData();
    this.users = new SQLUserData();
    this.gameNum = 1;
    try {
      configureDataBase();
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public void clear() throws DataAccessException {
    sessions.clear();
    users.clear();
    games.clear();
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
    return new AuthToken(userName, UUID.randomUUID().toString());
//    return sessions.newSession(userName);
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
    try {
      return games.listGames();
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
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


private final String[] createStatements = {"""
          CREATE TABLE IF NOT EXISTS users (
          username varchar(256) NOT NULL,
          password varchar(256) NOT NULL,
          email varchar(256) NOT NULL,
          json text default NULL,
          PRIMARY KEY (username)
          ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
          """,
          """
          CREATE TABLE IF NOT EXISTS games (
          gameID int NOT NULL,
          gameName varchar(256) NOT NULL,
          jsongame text NOT NULL,
          PRIMARY KEY (gameID),
          INDEX(gameName),
          INDEX(gameID)
          ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
          """,
          """
          CREATE TABLE IF NOT EXISTS authdata (
          authtoken varchar(256) NOT NULL,
          username varchar(256) NOT NULL,
          json TEXT DEFAULT NULL,
          PRIMARY KEY (authtoken)
          ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
          """
  };

  private void configureDataBase() throws DataAccessException {
    DatabaseManager.createDatabase();
    try(var conn = DatabaseManager.getConnection()) {
      for (var statement : createStatements) {
        try (var preparedStatement = conn.prepareStatement(statement)) {
          preparedStatement.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(500, String.format("Unable to configure database: %s", e.getMessage()));
    }
  }



}
