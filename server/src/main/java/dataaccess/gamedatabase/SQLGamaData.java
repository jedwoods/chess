package dataaccess.gamedatabase;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.authdatabase.AuthToken;

import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.executeUpdate;

public class SQLGamaData implements GameInterface{




  @Override
  public void remove(Integer currentID) {
    var statement = "DELETE FROM games WHERE gameID=?";
    try {
      executeUpdate(statement, currentID);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public GameData get(Integer currentID) {
    try (var conn =DatabaseManager.getConnection()){
      var statement = "SELECT * FROM games WHERE gameID =?";
      try (var ps = conn.prepareStatement(statement)){
        ps.setInt(1, currentID);
        try (var rs = ps.executeQuery()) {
          if (rs.next()){
            return new Gson().fromJson(rs.getString("jsongame"), GameData.class);
          }
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    } catch (DataAccessException e) {
      return null;
    }
    return null;
  }

  public String getWinner(Integer currentID){
    try (var conn =DatabaseManager.getConnection()){
      var statement = "SELECT * FROM games WHERE gameID =?";
      try (var ps = conn.prepareStatement(statement)){
        ps.setInt(1, currentID);
        try (var rs = ps.executeQuery()) {
          if (rs.next()){
            return rs.getString("winner");
          }
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    } catch (DataAccessException e) {
      return null;
    }
    return null;
  }

  @Override
  public void add(GameData token) {
    var statement = "INSERT INTO games (gameID, gameName, jsongame) VALUES (?, ?, ?)";
    var json = new Gson().toJson(token);
    try {
      var id=executeUpdate(statement, token.gameID(), token.gameName(), json);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public void add(GameData token, String winner) {
    var statement = "INSERT INTO games (gameID, gameName, jsongame, winner) VALUES (?, ?, ?, ?)";
    var json = new Gson().toJson(token);
    try {
      var id=executeUpdate(statement, token.gameID(), token.gameName(), json, winner);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int size() {
    try (var conn =DatabaseManager.getConnection()){
      var statement = "SELECT COUNT(*) FROM games";
      try (var ps = conn.prepareStatement(statement)){
        try (var rs = ps.executeQuery()) {
          if (rs.next()){
            return rs.getInt(1);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } catch (DataAccessException e) {
      throw new RuntimeException(e.getMessage());
    }
    return 0;
  }

  @Override
  public ArrayList<GameData> listGames() throws DataAccessException {
    var result = new ArrayList<GameData>();
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT gameID, jsongame FROM games";
      try (var ps = conn.prepareStatement(statement)) {
        try (var rs = ps.executeQuery()) {
          while (rs.next()) {
            result.add(new Gson().fromJson(rs.getString("jsongame"), GameData.class));
          }
        }
      }
    } catch (Exception e) {
      throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
    }
    return result;
  }


  public  int idx() throws DataAccessException {
      var conn = DatabaseManager.getConnection();
      String query = "SELECT MAX(gameID) FROM games";
      try (var statement = conn.prepareStatement(query)) {
        var resultSet = statement.executeQuery();
        if (resultSet.next()) {
          return resultSet.getInt(1); // Retrieves the maximum gameID
        }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      return 1;
  }


  public void clear(){
    var statement = "TRUNCATE games";
    try {
      executeUpdate(statement);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
