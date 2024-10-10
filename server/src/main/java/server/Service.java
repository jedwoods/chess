package server;
import dataaccess.*;
import com.google.gson.*;

import dataaccess.UserDataBase.user;
import dataaccess.authDataBase.*;
import dataaccess.GameDataBase.*;
import spark.Request;
import spark.Response;

import java.util.Objects;

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

}
