package server;
import dataaccess.*;
import com.google.gson.*;

import dataaccess.UserDataBase.user;
import dataaccess.authDataBase.*;
import dataaccess.GameDataBase.*;
import spark.Request;
import spark.Response;

public class Handler {
  Service service;
  public Handler(){
    service = new Service();

  }


  public Object clear(Request req, Response res){
    service.clear();
    assert service.isEmpty();
    res.type("applications/json");

    return new Gson().toJson(res.body());
  }

  public Object register(Request req, Response res) throws DataAccessException {
    String bodyStuff = req.body();
    user newuser = new Gson().fromJson(bodyStuff, user.class);
    authToken newToken = service.register(newuser);

    res.type("applications/json");
    res.body(new Gson().toJson(newToken));
    return new Gson().toJson(res.body());
  }

  public Object newGame(Request req, Response res){
    String bodyStuff = req.body();
    String token = req.headers("Authorization");
    String gameName = new Gson().fromJson(bodyStuff, gameData.class).gameName();
    if (service.confirmSession(token)){
      gameData game = service.addGame(gameName);
      res.body(String.valueOf(game.gameID()));
    }

    return new Gson().toJson(res.body());
  }

}
