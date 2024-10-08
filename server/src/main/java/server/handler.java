package server;

public class handler {
  authDataBase sessions;
  gameDataBase games;
  userDataBase users;

  public handler(){
    this.sessions = new authDataBase();
    this.games = new gameDataBase();
    this.users = new userDataBase();
  }

  public void clear(){
    sessions = new authDataBase();
    games = new gameDataBase();
    users = new userDataBase();
  }

public boolean isEmpty(){
    return games.size() == 0 && users.size() == 0 && sessions.size() == 0;
}

}
