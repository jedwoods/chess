package dataaccess;



import chess.ChessGame;
import dataaccess.userdatabase.*;
import dataaccess.gamedatabase.*;
import dataaccess.authdatabase.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class DataAccess implements DataAccessInterface{
  SQLUserData users;
  SQLGamaData games;
  SQLAuthData sessions;
  int gameNum;

  public DataAccess(){

    this.sessions = new SQLAuthData();
    this.games = new SQLGamaData();
    this.users = new SQLUserData();
    try {
      configureDataBase();
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
    this.gameNum = getIDX() + 1;
    if (this.gameNum < 1){
      this.gameNum = 1;
    }
  }

  public int getIDX(){
    try{
      return games.idx();
    } catch (DataAccessException e){
      return 1;
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

  public void addWinner(GameData game, String winner){
    games.remove(game.gameID());
    games.add(game, winner);
  }

  public String getWinner(int gameID){
    return this.games.getWinner(gameID);
  }

  public void removeGame(GameData game){
    games.remove(game.gameID());
  }

  public void logout(String token){

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

public void setTurn(GameData game, String turn){
    games.remove(game.gameID());
    games.setTurn(game, turn);
}


public String getTurn(GameData game){
    return games.getTurn(game.gameID());
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
          INDEX(gameID),
          winner varchar(256) default NULL,
          turn varchar(256) default white
          ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
          """,
          """
          CREATE TABLE IF NOT EXISTS authdata (
          authtoken varchar(256) NOT NULL,
          username varchar(256) NOT NULL,
          json TEXT DEFAULT NULL,
          PRIMARY KEY (authtoken)
          ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
          """,
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
