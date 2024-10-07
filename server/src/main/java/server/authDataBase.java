package server;

import java.util.ArrayList;
import java.util.Objects;

public class authDataBase implements dbClass<authToken, String> {
  ArrayList<authToken> tokenCollection = new ArrayList<>();

  @Override
  public void remove(String authToken) {
    tokenCollection.remove(this.get(authToken));
  }

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

}
