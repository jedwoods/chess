package server;

public class handler {
  authDataBase sessions;
  gameDataBase games;
  userDataBase users;

  private handler(authDataBase sessions, gameDataBase games, userDataBase users){
    this.sessions = sessions;
    this.games = games;
    this.users = users;
  }

  public void clear(){
    sessions = new authDataBase();
    games = new gameDataBase();
    users = new userDataBase();
  }



}
