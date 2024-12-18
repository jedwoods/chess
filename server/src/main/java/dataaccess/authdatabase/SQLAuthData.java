package dataaccess.authdatabase;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.executeUpdate;

public class SQLAuthData implements DataBaseInterface<AuthToken, String> {

  @Override
  public void remove(String tokenType)  {
    var statement = "DELETE FROM authdata WHERE authtoken=?";
    try {
      executeUpdate(statement, tokenType);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public AuthToken get(String tokenType) {
    try (var conn =DatabaseManager.getConnection()){
      var statement = "SELECT * FROM authdata WHERE authtoken =?";
      try (var ps = conn.prepareStatement(statement)){
        ps.setString(1, tokenType);
        try (var rs = ps.executeQuery()) {
              if (rs.next()){
                return new Gson().fromJson(rs.getString("json"), AuthToken.class);
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
  public void add(AuthToken token) {
    var statement = "INSERT INTO authdata (authtoken, username, json) VALUES (?, ?, ?)";
    var json = new Gson().toJson(token);
    try {
      var id=executeUpdate(statement, token.authToken(), token.username(), json);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int size() {
    try (var conn =DatabaseManager.getConnection()){
      var statement = "SELECT COUNT(*) FROM authdata";
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

  public void clear()  {
    var statement = "TRUNCATE authdata";
    try {
      executeUpdate(statement);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }


}
