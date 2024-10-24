package dataaccess;

import dataaccess.authdatabase.AuthToken;
import dataaccess.gamedatabase.GameData;
import dataaccess.gamedatabase.GameResponse;
import dataaccess.userdatabase.User;
import org.junit.jupiter.api.Test;
import server.JoinRequest;
import server.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

  Service service = new Service();

  @Test
  void clear() throws DataAccessException {
    this.service.register(new User("girl", "password", "@gmail"));
    assert !this.service.getDB().isEmpty();
    this.service.clear();
    assert this.service.getDB().isEmpty();
  }

  @Test
  void register() throws DataAccessException {
    service.clear();
    User newUser = new User("girl", "password", "@gmail");
    AuthToken token = this.service.register(newUser);
    assert Objects.equals(token.username(), newUser.username());
    assert !this.service.getDB().isEmpty();
    service.clear();
  }

  @Test
  void registerNegative(){
    User newUser = new User("girl", null, "@gmail");
    assertThrows(DataAccessException.class, () -> this.service.register(newUser));
  }

  @Test
  void newGame() throws DataAccessException {
    service.clear();
    User validUser = new User("girl", "password", "@gmail");
    AuthToken token = service.register(validUser);
    GameResponse gameResponse = service.newGame(token.authToken(), "our Game");
    assert !this.service.getDB().isEmpty();
    assert  gameResponse.gameID() > 0;
  }

  @Test
  void newGameNegative() {
    service.clear();
    assertThrows(DataAccessException.class, () -> service.newGame("BadUsername", null));
  }

  @Test
  void logout() throws DataAccessException {
    this.service.clear();
    User newUser = new User("girl", "password", "@gmail");
    AuthToken token = this.service.register(newUser);
    service.logout(token.authToken());
    assertNull(this.service.getDB().getSession(token.authToken()));
  }

  @Test
  void logoutNegative() {
    this.service.clear();
    assertThrows(DataAccessException.class, () -> service.logout("Badtoken"));
  }

  @Test
  void login() throws DataAccessException {
    this.service.clear();
    User newUser = new User("girl", "password", "@gmail");
    AuthToken token = this.service.register(newUser);
    assert Objects.equals(token.username(), newUser.username());
    AuthToken newtoken = this.service.login(newUser);
    assert ! Objects.equals(newtoken, token);
    assert Objects.equals(token.username(), newtoken.username());

  }

  @Test
  void loginNegative() {
    User invalidUser = new User("","","");
    assertThrows(DataAccessException.class, () -> service.login(invalidUser));
  }

  @Test
  void loginWrongPassword() throws DataAccessException {
    User newUser=new User("girl", "password", "@gmail");
    AuthToken token=this.service.register(newUser);
    User invalid = new User("girl", "badPassword", null);

    assertThrows(DataAccessException.class, () -> this.service.login(invalid));
  }


  @Test
  void nullUserLogin(){
    User invalidUser = new User(null,"","");
    assertThrows(DataAccessException.class, () -> service.login(invalidUser));
  }

  @Test
  void listGames() throws DataAccessException {
    this.service.clear();
    User newUser = new User("girl", "password", "@gmail");
    AuthToken token = this.service.register(newUser);
    GameResponse gameResponse = this.service.newGame(token.authToken(), "new game");
    GameData game = this.service.getDB().getGame(gameResponse.gameID());
    Map<String, HashSet<GameData>> temp = this.service.listGames(token.authToken());
    System.out.println(this.service.listGames(token.authToken()));
    assert this.service.listGames(token.authToken()).get("games").size() == 1;

  }

  @Test
  void listGamesNegative() {
    this.service.clear();
    assertThrows(DataAccessException.class, ()-> this.service.listGames("bad Token"));
  }

  @Test
  void joinGame() throws DataAccessException {
    this.service.clear();
    User newUser = new User("girl", "password", "@gmail");
    AuthToken token = this.service.register(newUser);
    GameResponse gameResponse = this.service.newGame(token.authToken(), "new game");
    this.service.joinGame(token.authToken(), new JoinRequest("BLACK", gameResponse.gameID()));
    assert Objects.equals(this.service.getDB().getGame(gameResponse.gameID()).blackUsername(), newUser.username());

  }

  @Test
  void joinGameNegative() throws DataAccessException {
    this.service.clear();
    User newUser = new User("girl", "password", "@gmail");
    User badUser = new User("badguy", "password2", "@hotmail");
    AuthToken token = this.service.register(newUser);
    AuthToken badToken = this.service.register(badUser);
    GameResponse gameResponse = this.service.newGame(token.authToken(), "new game");
    this.service.joinGame(token.authToken(), new JoinRequest("BLACK", gameResponse.gameID()));
    assertThrows(DataAccessException.class, ()-> this.service.joinGame(badToken.authToken(), new JoinRequest("BLACK", gameResponse.gameID())));
  }

}