package dataaccess.authdatabase;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class AuthDataBase implements DataBaseInterface<AuthToken, String> {
  ArrayList<AuthToken> tokenCollection = new ArrayList<>();

  @Override
  public void remove(String authToken) {
    tokenCollection.remove(this.get(authToken));
  }

  @Override
  public AuthToken get(String authToken) {
    for (var obj : tokenCollection){
      if (Objects.equals(obj.authToken(), authToken)){
        return obj;
      }
    }
      return null;
  }

  @Override
  public void add(AuthToken token) {
    tokenCollection.add(token);

  }

  @Override
  public int size() {
    return tokenCollection.size();
  }


}
