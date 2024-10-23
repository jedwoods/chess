package dataaccess;

import dataaccess.authdatabase.AuthToken;
import dataaccess.gamedatabase.GameData;
import dataaccess.userdatabase.User;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLDataAccess implements DataAccessInterface{


  public SQLDataAccess() throws DataAccessException {
//    configureDataBase();
  }

//  private final String[] createStatements = {"""
//          CREATE TABLE IF NOT EXISTS users (
//          'username' varchar(256) NOT NULL,
//          'password' varchar(256) NOT NULL,
//          'email' varchar(256) NOT NULL,
//          PRIMARY KEY (username)
//          ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
//          """,
//          """
//          CREATE TABLE IF NOT EXISTS games (
//          'gameID' int NOT NULL,
//          'whiteUsername' DEFAULT NULL,
//          'blackUsername' DEFAULT NULL,
//          'gameName' varchar(256) NOT NULL,
//          'game' text NOT NULL,
//          PRIMARY KEY 'gameName',
//          INDEX(gameName),
//          INDEX(gameID),
//          ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
//          """,
//          """
//          CREATE TABLE IF NOT EXISTS authData (
//          'authtoken' varchar(256) NOT NULL,
//          'username' varchar(256) NOT NULL,
//          PRIMARY KEY 'authtoken'
//          ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
//          """
//  };
//
//  private void configureDataBase() throws DataAccessException {
//    try(var conn = DatabaseManager.getConnection()) {
//      for (var statement : createStatements) {
//        try (var preparedStatement = conn.prepareStatement(statement)) {
//          preparedStatement.executeUpdate();
//        }
//      }
//    } catch (SQLException e) {
//      throw new DataAccessException(500, String.format("Unable to configure database: %s", e.getMessage()));
//    }
//  }

  @Override
  public void clear() {

  }

  @Override
  public User userCheck(String userName) {
    return null;
  }

  @Override
  public void addUser(User newUser) {

  }

  @Override
  public User getUser(String username) {
    return null;
  }

  @Override
  public AuthToken makeToken(String userName) {
    return null;
  }

  @Override
  public void addToken(AuthToken token) {

  }

  @Override
  public boolean confirmSession(String token) {
    return false;
  }

  @Override
  public GameData addGame(String gameName) {
    return null;
  }

  @Override
  public void reAddGame(GameData game) {

  }

  @Override
  public void removeGame(GameData game) {

  }

  @Override
  public void logout(String token) {

  }

  @Override
  public ArrayList<GameData> listGames() {
    return null;
  }

  @Override
  public GameData getGame(int gameID) {
    return null;
  }

  @Override
  public AuthToken getSession(String token) {
    return null;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }
}
