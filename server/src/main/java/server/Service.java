package server;
import dataaccess.*;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.authDataBase.authToken;
import dataaccess.UserDataBase.*;
import org.eclipse.jetty.server.Authentication;

public class Service {
  DataAccess dataAccess;

  public Service(){
    dataAccess= new DataAccess();
  }

  public Object clear(){
    dataAccess.clear();
    assert dataAccess.isEmpty();
    return new Gson().toJson(new EmptyMessage());

  }

  public Object register(user newuser) throws DataAccessException {

    if (newuser.username() == null || newuser.password() == null || newuser.email() == null){
      throw new DataAccessException(400, ("invalid information"));
    }

    user usercheck = dataAccess.userCheck(newuser.username());
    if (usercheck != null){
      throw new DataAccessException(403, "Forbidden unauthorized");
//      res.status(403);
//      res.body("Error: Forbidden Unauthorized");
//      return new Gson().toJson(new ErrorMessage("Error: Forbidden Unauthorized"));

    }
    dataAccess.addUser(newuser);
    authToken token = dataAccess.makeToken(newuser.username());
    dataAccess.addToken(token);
    return token;
  }


}
