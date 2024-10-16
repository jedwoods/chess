package dataAccess.userDataBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDataBase implements UserDataInterface{
  List<User> Users;
  public UserDataBase(){
    Users= new ArrayList<>();

  }


  @Override
  public void remove(String username) {

  }

  @Override
  public User get(String username) {
    for (User currentUser : Users){
      if( Objects.equals(username, currentUser.username())){
        return currentUser;
      }
    }
    return null;
  }

  @Override
  public void add(User currentUser) {
    for (var user : Users){
      if (Objects.equals(user.username(), currentUser.username()) || Objects.equals(user.email(), currentUser.email()) || Objects.equals(user.password(), currentUser.password())){
        return;
      }
    }
    Users.add(currentUser);
  }

  @Override
  public int size() {
    return Users.size();
  }
}
