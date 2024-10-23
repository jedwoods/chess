package dataaccess.authdatabase;

public class SQLAuthData implements DataBaseInterface<AuthToken, String> {

  @Override
  public void remove(String tokenType) {

  }

  @Override
  public AuthToken get(String tokenType) {
    return null;
  }

  @Override
  public void add(AuthToken token) {

  }

  @Override
  public int size() {
    return 0;
  }

  public void clear(){}

}
