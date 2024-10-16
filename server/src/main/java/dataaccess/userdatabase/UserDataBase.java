package dataaccess.userdatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDataBase implements UserDataInterface{
  List<User> users;
  public UserDataBase(){
    users= new ArrayList<>();

  }


  @Override
  public void remove(String username) {

  }

  @Override
  public User get(String username) {
    for (User currentUser : users){
      if( Objects.equals(username, currentUser.username())){
        return currentUser;
      }
    }
    return null;
  }

  @Override
  public void add(User currentUser) {
    for (var user : users){
      boolean userBool = Objects.equals(user.username(), currentUser.username());
      boolean emailBool = Objects.equals(user.email(), currentUser.email());
      if ( userBool || emailBool || Objects.equals(user.password(), currentUser.password())){
        return;
      }
    }
    users.add(currentUser);
  }

  @Override
  public int size() {
    return users.size();
  }
}
