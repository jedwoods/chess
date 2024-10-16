package dataaccess.userdatabase;

public interface UserDataInterface {
  public User get(String username);
  public void remove(String username);
  public void add(User currentUser);
  public int size();
}
