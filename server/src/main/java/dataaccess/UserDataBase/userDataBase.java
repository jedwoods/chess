package dataaccess.UserDataBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class userDataBase implements UserDataInterface{
  List<user> users;
  public userDataBase(){
    users = new ArrayList<>();

  }


  @Override
  public void remove(String username) {

  }

  @Override
  public user get(String username) {
    return null;
  }

  @Override
  public void add(user currentUser) {
    for (var user : users){
      if (Objects.equals(user.username(), currentUser.username()) || Objects.equals(user.email(), currentUser.email()) || Objects.equals(user.password(), currentUser.password())){
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
