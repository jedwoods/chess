package dataAccess.userDataBase;

public interface UserDataInterface {
  public User get(String Username);
  public void remove(String username);
  public void add(User currentUser);
  public int size();
}
