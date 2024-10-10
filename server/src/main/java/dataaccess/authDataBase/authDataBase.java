package dataaccess.authDataBase;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class authDataBase implements dbClass<authToken, String> {
  ArrayList<authToken> tokenCollection = new ArrayList<>();

  @Override
  public void remove(String authToken) {
    tokenCollection.remove(this.get(authToken));
  }

  @Override
  public authToken get(String authToken) {
    for (var obj : tokenCollection){
      if (Objects.equals(obj.authToken(), authToken)){
        return obj;
      }
    }
      return null;
  }

  @Override
  public void add(authToken token) {
    tokenCollection.add(token);

  }

  @Override
  public int size() {
    return tokenCollection.size();
  }

  public authToken newSession(String username){
    return new authToken(username, UUID.randomUUID().toString());


  }

}
