package dataaccess.userdatabase;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.authdatabase.AuthToken;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.executeUpdate;

public class SQLUserData implements UserDataInterface{

  @Override
  public User get(String username) {
    try (var conn =DatabaseManager.getConnection()){
      var statement = "SELECT * FROM users WHERE username =?";
      try (var ps = conn.prepareStatement(statement)){
        ps.setString(1, username);
        try (var rs = ps.executeQuery()) {
          if (rs.next()){
            return new Gson().fromJson(rs.getString("json"), User.class);
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
  public void remove(String username) {
    var statement = "DELETE FROM users WHERE username=?";
    try {
      executeUpdate(statement, username);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }}

  @Override
  public void add(User currentUser) {
    var statement = "INSERT INTO users (username, password, email, json) VALUES (?, ?, ?, ?)";
    var json = new Gson().toJson(currentUser);
    try {
      var id=executeUpdate(statement, currentUser.username(), currentUser.password(), currentUser.email(), json);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int size() {
    try (var conn =DatabaseManager.getConnection()){
      var statement = "SELECT COUNT(*) FROM users";
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

  public void clear(){
    var statement = "TRUNCATE users";
    try {
      executeUpdate(statement);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  }


