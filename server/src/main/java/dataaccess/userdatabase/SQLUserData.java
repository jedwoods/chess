package dataaccess.userdatabase;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.SQLException;

public class SQLUserData implements UserDataInterface{

  @Override
  public User get(String username) {
    return null;
  }

  @Override
  public void remove(String username) {

  }

  @Override
  public void add(User currentUser) {

  }

  @Override
  public int size() {
    return 0;
  }

  public void clear(){}

  }


