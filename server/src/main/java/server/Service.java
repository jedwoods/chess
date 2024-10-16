package server;
import dataAccess.*;

import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.authDataBase.AuthToken;
import dataAccess.userDataBase.*;

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

  public Object register(User newuser) throws DataAccessException {

    if (newuser.username() == null || newuser.password() == null || newuser.email() == null){
      throw new DataAccessException(400, ("invalid information"));
    }

    User usercheck = dataAccess.userCheck(newuser.username());
    if (usercheck != null){
      throw new DataAccessException(403, "Forbidden unauthorized");
//      res.status(403);
//      res.body("Error: Forbidden Unauthorized");
//      return new Gson().toJson(new ErrorMessage("Error: Forbidden Unauthorized"));

    }
    dataAccess.addUser(newuser);
    AuthToken token = dataAccess.makeToken(newuser.username());
    dataAccess.addToken(token);
    return token;
  }


}
