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


public class newHandler {
  Service service;
  DataAccess dataAccess;

  public newHandler() {
    this.service = new Service();
    this.dataAccess = service.dataAccess;
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











}
