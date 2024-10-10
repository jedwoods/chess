package server;
import dataaccess.*;
import com.google.gson.*;

import dataaccess.UserDataBase.user;
import dataaccess.authDataBase.*;
import dataaccess.GameDataBase.*;
import spark.Request;
import spark.Response;

import java.util.HashSet;
import java.util.Objects;
import java.util.Map;

public class Service {
  DataAccess dataAccess;
  public Service(){
    dataAccess= new DataAccess();

  }





  public Object clear(Request req, Response res) throws DataAccessException {
    try {
      dataAccess.clear();
      assert dataAccess.isEmpty();
      res.type("applications/json");

      return new Gson().toJson(new EmptyMessage());
    } catch (Exception e) {
      res.status(500);
      throw new DataAccessException(500, "Clear function broke down :(");
    }
  }

  public Object register(Request req, Response res) throws DataAccessException {
    String bodyStuff = req.body();
    user newuser = new Gson().fromJson(bodyStuff, user.class);
    if (newuser.username() == null || newuser.password() == null || newuser.email() == null){
      res.status(400);
      res.body("Error: Bad Request");
      return new Gson().toJson(new ErrorMessage("Error: Bad Request"));
    }

    user usercheck = dataAccess.userCheck(newuser.username());
    if (usercheck != null){
      res.status(403);
      res.body("Error: Forbidden Unauthorized");
      return new Gson().toJson(new ErrorMessage("Error: Forbidden Unauthorized"));

    }
    dataAccess.addUser(newuser);
    authToken token = dataAccess.makeToken(newuser.username());
    dataAccess.addToken(token);

    res.type("applications/json");
//    res.body(new Gson().toJson(newToken));
    return new Gson().toJson(token);
  }

  public Object newGame(Request req, Response res) throws DataAccessException{
    String bodyStuff = req.body();
    String token = req.headers("Authorization");
    String gameName = new Gson().fromJson(bodyStuff, gameData.class).gameName();
    if (dataAccess.confirmSession(token)){
      gameData game = dataAccess.addGame(gameName);
      res.body(String.valueOf(game.gameID()));
      return new Gson().toJson(new GameResponse(game.gameID()));
    }
    res.status(401);
    return new Gson().toJson(new ErrorMessage("Error: unauthorized bad request"));
  }

  public Object logout(Request req, Response res){
    String token = req.headers("Authorization");
    if (!dataAccess.confirmSession(token)){
      res.status(401);
      return new Gson().toJson(new ErrorMessage("Error: unauthorized bad request"));
    }
    dataAccess.logout(token);
    res.body("");
    return new Gson().toJson(new EmptyMessage());
  }




  public Object login(Request req, Response res){
    String bodyStuff = req.body();
    user currentUser =  new Gson().fromJson(bodyStuff, user.class);
    if (dataAccess.userCheck(currentUser.username()) == null){
      res.status(401);
      return new Gson().toJson(new ErrorMessage("Error: unauthorized bad request"));
    }

    String userPassword = dataAccess.getUser(currentUser.username()).password();
    if (!Objects.equals(userPassword, currentUser.password())){
      res.status(401);
      return new Gson().toJson(new ErrorMessage("Error: unauthorized bad request, bad password"));
    }
    currentUser = dataAccess.getUser(currentUser.username());
    authToken token = dataAccess.makeToken(currentUser.username());
    dataAccess.addToken(token);
    return new Gson().toJson(token);
  }

  public Object listGames(Request req, Response res){
    String token = req.headers("Authorization");
    if (!dataAccess.confirmSession(token)){
      res.status(401);
      return new Gson().toJson(new ErrorMessage("Error: unauthorized bad request"));
    }

    return new Gson().toJson(Map.of("games", new HashSet<>(dataAccess.listGames())));}





  public Object joinGame(Request req, Response res){
    String token = req.headers("Authorization");

    if (!dataAccess.confirmSession(token)){
      res.status(401);
      return new Gson().toJson(new ErrorMessage("Error: unauthorized bad request"));
    }

    String newUsername = dataAccess.getSession(token).username();
    String bodyStuff = req.body();
    joinRequest currentUser =  new Gson().fromJson(bodyStuff, joinRequest.class);

    gameData currentGame = dataAccess.getGame(currentUser.gameID());
    if (currentGame == null){
      res.status(400);
      return new Gson().toJson(new ErrorMessage("Error: bad request"));
    }

    if (!Objects.equals(currentUser.playerColor(), "BLACK") && !Objects.equals(currentUser.playerColor(), "WHITE") && !Objects.equals(currentUser.playerColor(), "")){
      res.status(400);
      return new Gson().toJson(new ErrorMessage("Error: Not an actual color"));
    }


    if (Objects.equals(currentUser.playerColor(), "WHITE") && currentGame.whiteUsername() == null){
      dataAccess.removeGame(currentGame);
      dataAccess.reAddGame(new gameData(currentGame.gameID(), newUsername, currentGame.blackUsername(), currentGame.gameName(), currentGame.game()));
      return new Gson().toJson(new EmptyMessage());
    } else if (Objects.equals(currentUser.playerColor(), "BLACK") && currentGame.blackUsername() == null) {
      dataAccess.removeGame(currentGame);
      dataAccess.reAddGame(new gameData(currentGame.gameID(),  currentGame.whiteUsername(), newUsername, currentGame.gameName(), currentGame.game() ) );
      return new Gson().toJson(new EmptyMessage());
    } else if (Objects.equals(currentUser.playerColor(), "")) {
      return new Gson().toJson(new EmptyMessage());
    }
    res.status(403);
    return new Gson().toJson(new ErrorMessage("Error: Already taken Forbidden"));
  }



}
