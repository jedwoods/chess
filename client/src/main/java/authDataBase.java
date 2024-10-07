import java.util.ArrayList;

public class authDataBase{
  ArrayList<authToken> tokenCollection = new ArrayList<>();


  public void remove(authToken tokenObject) {
    tokenCollection.remove(tokenObject);
  }


  public void get(String authToken) {

  }

}
