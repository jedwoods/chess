package server;
import dataaccess.*;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.authdatabase.AuthToken;
import dataaccess.gamedatabase.GameData;
import dataaccess.gamedatabase.GameResponse;
import dataaccess.userdatabase.*;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.mindrot.jbcrypt.*;

public class Service {
  DataAccess dataAccess;
  public Service(){
    dataAccess= new DataAccess();
  }
  public DataAccess getDB(){
    return this.dataAccess;
  }


  public void clear(){
    try {
      dataAccess.clear();
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
    assert dataAccess.isEmpty();
  }


  public AuthToken register(User newuser) throws DataAccessException {
    if (newuser.username() == null || newuser.password() == null || newuser.email() == null){
      throw new DataAccessException(400, ("invalid information"));
    }
    newuser = new User(newuser.username(), BCrypt.hashpw(newuser.password(), BCrypt.gensalt()), newuser.email());
    User usercheck = dataAccess.userCheck(newuser.username());
    if (usercheck != null){
      throw new DataAccessException(403, "Forbidden unauthorized");
    }
    dataAccess.addUser(newuser);
    AuthToken token = dataAccess.makeToken(newuser.username());
    dataAccess.addToken(token);
    return token;
  }


  public GameResponse newGame(String token, String gameName) throws DataAccessException {
    if (dataAccess.confirmSession(token)){
      GameData game = dataAccess.addGame(gameName);
      return new GameResponse(game.gameID());
    }
    throw new DataAccessException(401, "Error: unauthorized bad request");
  }


  public Object joinGame(String token, JoinRequest currentUser) throws DataAccessException {
    if (!dataAccess.confirmSession(token)){
      throw new DataAccessException(401,"Error: unauthorized bad request");
    }

    String newUsername = dataAccess.getSession(token).username();
    GameData currentGame = dataAccess.getGame(currentUser.gameID());
    if (currentGame == null){
      throw new DataAccessException(400,"Error: bad request" );
    }
    boolean blackBool = Objects.equals(currentUser.playerColor().toLowerCase(), "black");
    boolean whiteBool = Objects.equals(currentUser.playerColor().toLowerCase(), "white");
    if (!blackBool && !whiteBool && Objects.equals(currentUser.playerColor(), "")){
      throw new DataAccessException(400,"Error: Not an actual color");

    }
    if (Objects.equals(currentUser.playerColor().toLowerCase(), "white") && currentGame.whiteUsername() == null){
      dataAccess.removeGame(currentGame);
      dataAccess.reAddGame(new GameData(currentGame.gameID(), newUsername, currentGame.blackUsername(), currentGame.gameName(), currentGame.game()));
      return new EmptyMessage();
    } else if (blackBool && currentGame.blackUsername() == null) {
      dataAccess.removeGame(currentGame);
      GameData newGame = new GameData(currentGame.gameID(),currentGame.whiteUsername(),newUsername,currentGame.gameName(),currentGame.game());
      dataAccess.reAddGame( newGame);
      return new EmptyMessage();
    } else if (Objects.equals(currentUser.playerColor(), "")) {
      return new EmptyMessage();
    }
    throw new DataAccessException(403,"Error: Already taken Forbidden");
  }


  public Map<String, HashSet<GameData>> listGames(String token) throws DataAccessException {
    if (!dataAccess.confirmSession(token)){
      throw new DataAccessException(401,"Error: unauthorized bad request");

    }
    return Map.of("games", new HashSet<>(dataAccess.listGames()));
  }

  public AuthToken login(User currentUser) throws DataAccessException {
    if (dataAccess.userCheck(currentUser.username()) == null){
      throw new DataAccessException(401,"Error: unauthorized bad request" );
    }
    String userPassword = dataAccess.getUser(currentUser.username()).password();
    if (!BCrypt.checkpw(currentUser.password(), userPassword)){
      throw new DataAccessException(401,"Error: unauthorized bad request, bad password" );
    }
    currentUser = dataAccess.getUser(currentUser.username());
    AuthToken token = dataAccess.makeToken(currentUser.username());
    dataAccess.addToken(token);
    return token;
  }

  public EmptyMessage logout(String token) throws DataAccessException {
    if (!dataAccess.confirmSession(token)){
      throw new DataAccessException(401,"Error: unauthorized bad request" );
    }
    dataAccess.logout(token);
    return new EmptyMessage();
  }

}
