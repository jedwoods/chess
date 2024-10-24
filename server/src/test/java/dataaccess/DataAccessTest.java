package dataaccess;

import dataaccess.authdatabase.AuthToken;
import dataaccess.gamedatabase.GameData;
import dataaccess.gamedatabase.GameResponse;
import dataaccess.userdatabase.User;
import org.junit.jupiter.api.Test;
import server.Service;

import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {
  Service service = new Service();
  DataAccess dataAccess = service.getDB();

  @Test
  void clear() throws DataAccessException {
    User newUser = new User("girl", "password", "@gmail");
    service.register(newUser);
    service.clear();
    assert dataAccess.isEmpty();
  }

  @Test
  void notClear(){
    service.clear();
    User newUser = new User("girl", null, "@gmail");
    assertThrows(DataAccessException.class, () -> service.register(newUser));
  }

  @Test
  void userCheck() throws DataAccessException {
    service.clear();
    User newUser = new User("girl", "password", "@gmail");
    service.register(newUser);
    assertEquals(newUser.username(), dataAccess.userCheck(newUser.username()).username());
    service.clear();

  }

  @Test
  void addUser() throws DataAccessException {
    service.clear();
    User newUser = new User("girl", "password", "@gmail");
    service.register(newUser);
    assert !dataAccess.isEmpty();
    service.clear();
  }

  @Test
  void getUser() throws DataAccessException {
    service.clear();
    User newUser = new User("newBoy", "password", "@gmail");
    service.register(newUser);
    assertEquals(newUser.username(), dataAccess.getUser(newUser.username()).username());
    service.clear();
  }

  @Test
  void makeToken() {
    assertNotNull(dataAccess.makeToken("username"));
  }

  @Test
  void makeToken2(){
    AuthToken token1 = dataAccess.makeToken("username");
    AuthToken token2 =  dataAccess.makeToken("other username");
    assertNotEquals(token1, token2);
  }


  @Test
  void addToken() {
    service.clear();
    User newUser = new User("newBoy", "password", "@gmail");
    dataAccess.addToken(dataAccess.makeToken(newUser.username()));
    assert !dataAccess.isEmpty();
  }

  @Test
  void addTwoTokens(){
    service.clear();
    User newUser = new User("newBoy", "password", "@gmail");
    User secondusesr = new User("newBoy", null, "@gmail");
    dataAccess.addToken(dataAccess.makeToken(newUser.username()));
    dataAccess.addToken(dataAccess.makeToken(secondusesr.username()));
    assert !dataAccess.isEmpty();
  }

  @Test
  void addbadTokens(){
    service.clear();
    User secondusesr = new User("newBoy", null, "@gmail");
    if (dataAccess.makeToken(secondusesr.username()) == null){
    dataAccess.addToken(dataAccess.makeToken(secondusesr.username()));}
    assert dataAccess.isEmpty();
  }





  @Test
  void confirmSession() {
    service.clear();
    User newUser = new User("newBoy", "password", "@gmail");
    AuthToken token = dataAccess.makeToken(newUser.username());
    dataAccess.addToken(token);
    assert dataAccess.confirmSession(token.authToken());
    assert !dataAccess.confirmSession("randoName");
  }

  @Test
  void addGame() throws DataAccessException {
    service.clear();
    User validUser = new User("girl", "password", "@gmail");
    AuthToken token = service.register(validUser);
    GameResponse gameResponse = service.newGame(token.authToken(), "our Game");
    assert !dataAccess.isEmpty();
  }

  @Test
  void reAddGame() throws DataAccessException {
    service.clear();
    User validUser = new User("girl", "password", "@gmail");
    AuthToken token = service.register(validUser);
    GameResponse gameResponse = service.newGame(token.authToken(), "our Game");
    GameData game = dataAccess.getGame(gameResponse.gameID());
    dataAccess.removeGame(game);
    dataAccess.reAddGame(game);
    assert !dataAccess.isEmpty();
    assertEquals(game.gameID(), dataAccess.getGame(gameResponse.gameID()).gameID());
  }

  @Test
  void removeGame() throws DataAccessException {
    service.clear();
    User validUser = new User("girl", "password", "@gmail");
    AuthToken token = service.register(validUser);
    GameResponse gameResponse = service.newGame(token.authToken(), "our Game");
    GameData game = dataAccess.getGame(gameResponse.gameID());
    dataAccess.removeGame(game);
    assertNull(dataAccess.getGame(game.gameID()));
  }

  @Test
  void logout() throws DataAccessException {
    service.clear();
    User validUser = new User("girl", "password", "@gmail");
    AuthToken token = service.register(validUser);
    dataAccess.logout(token.authToken());
    assertNull(dataAccess.getSession(token.username()));
  }

  @Test
  void invalidLogout(){
    service.clear();
    dataAccess.logout("invalid token");
    assert dataAccess.isEmpty();
  }

  @Test
  void listGames() throws DataAccessException {
    this.service.clear();
    User newUser = new User("girl", "password", "@gmail");
    AuthToken token = service.register(newUser);
    service.newGame(token.authToken(), "new game");
    assert this.service.listGames(token.authToken()).get("games").size() == 1;
  }




  @Test
  void getGame() throws DataAccessException {
    service.clear();
    User validUser = new User("girl", "password", "@gmail");
    AuthToken token = service.register(validUser);
    GameResponse gameResponse = service.newGame(token.authToken(), "our Game");
    GameData game = dataAccess.getGame(gameResponse.gameID());
    assertNotNull(dataAccess.getGame(game.gameID()));
  }



  @Test
  void nullUserLogin(){
    service.clear();
    assertNull(dataAccess.getUser("nonanmaasdf"));
  }



  @Test
  void getSession() throws DataAccessException {
    service.clear();
    User validUser = new User("girl", "password", "@gmail");
    AuthToken token = service.register(validUser);
    assertEquals(dataAccess.getSession(token.authToken()), token);
  }



  @Test
  void isEmpty() {
    service.clear();
    assert dataAccess.isEmpty();
  }


  @Test
  void notEmpty() throws DataAccessException {
    service.clear();
    User newUser = new User("girl", "password", "@gmail");
    service.register(newUser);
    assert !dataAccess.isEmpty();
  }
}