package dataaccess.UserDataBase;

public interface UserDataInterface {
  public user get(String Username);
  public void remove(String username);
  public void add(user currentUser);
  public int size();
}
