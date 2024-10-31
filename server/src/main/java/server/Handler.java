package server;
import dataaccess.*;
import com.google.gson.*;

import dataaccess.userdatabase.User;
import dataaccess.authdatabase.*;
import dataaccess.gamedatabase.*;
import spark.Request;
import spark.Response;

import java.util.HashSet;
import java.util.Objects;
import java.util.Map;


public class Handler {
  Service service;

  public Handler(){
    this.service = new Service();

  }

  public Object clear(Request req, Response res) throws DataAccessException {
    try {

      service.clear();
      assert service.dataAccess.isEmpty();
      res.type("applications/json");
      return new Gson().toJson(new EmptyMessage());
    } catch (Exception e) {
      res.status(500);
      throw new DataAccessException(500, "Clear function broke down :(");
    }
  }


  public Object register(Request req, Response res) throws DataAccessException {
    String bodyStuff = req.body();
    User newuser = new Gson().fromJson(bodyStuff, User.class);
    try {
      return new Gson().toJson(service.register(newuser));
    }catch (DataAccessException e){
      res.status(e.statusCode());
      return new Gson().toJson(new ErrorMessage("Error: Forbidden Unauthorized"));
    }

  }


  public Object newGame(Request req, Response res) throws DataAccessException{
    String bodyStuff = req.body();
    String token = req.headers("Authorization");
    String gameName = new Gson().fromJson(bodyStuff, GameData.class).gameName();

    try {
      return new Gson().toJson(service.newGame(token, gameName));
    }catch (DataAccessException e){
      res.status(e.statusCode());
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    }
  }


  public Object logout(Request req, Response res){
    String token = req.headers("Authorization");
    try {
      return new Gson().toJson(service.logout(token));
    }catch (DataAccessException e){
      res.status(e.statusCode());
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    }
  }


  public Object login(Request req, Response res){
    String bodyStuff = req.body();
    User currentUser =  new Gson().fromJson(bodyStuff, User.class);
    try {
      return new Gson().toJson(service.login(currentUser));
    }catch (DataAccessException e){
      res.status(e.statusCode());
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    }

  }


  public Object listGames(Request req, Response res) {
    String token=req.headers("Authorization");
    try {
      return new Gson().toJson(service.listGames(token));
    }catch (DataAccessException e){
      res.status(e.statusCode());
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    }
  }


  public Object joinGame(Request req, Response res){
    String token = req.headers("Authorization");
    String bodyStuff = req.body();
    JoinRequest currentUser =  new Gson().fromJson(bodyStuff, JoinRequest.class);
    try {
      return new Gson().toJson(service.joinGame(token, currentUser));
    }catch (DataAccessException e){
      res.status(e.statusCode());
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    }
  }


}
