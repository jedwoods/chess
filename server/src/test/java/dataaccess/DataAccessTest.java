package dataaccess;

import dataaccess.userdatabase.User;
import org.junit.jupiter.api.Test;
import server.Service;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {
  Service service = new Service();
  DataAccess dataAccess = service.getDB();

  @Test
  void clear() throws DataAccessException {
    User newUser = new User("girl", null, "@gmail");
    service.register(newUser);
    service.clear();
    assert dataAccess.isEmpty();

  }

  @Test
  void userCheck() {
  }

  @Test
  void addUser() {
  }

  @Test
  void getUser() {
  }

  @Test
  void makeToken() {
  }

  @Test
  void addToken() {
  }

  @Test
  void confirmSession() {
  }

  @Test
  void addGame() {
  }

  @Test
  void reAddGame() {
  }

  @Test
  void removeGame() {
  }

  @Test
  void logout() {
  }

  @Test
  void listGames() {
  }

  @Test
  void getGame() {
  }

  @Test
  void getSession() {

  }

  @Test
  void isEmpty() {
    service.clear();
    assert dataAccess.isEmpty();
  }
  @Test
  void notEmpty() throws DataAccessException {
    service.clear();
    User newUser = new User("girl", null, "@gmail");
    service.register(newUser);
    assert !dataAccess.isEmpty();
  }
}