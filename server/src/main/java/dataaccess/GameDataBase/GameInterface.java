package dataaccess.GameDataBase;

import java.util.ArrayList;

public interface GameInterface {
  public void remove(Integer currentID);
  public gameData get(Integer currentID);
  public void add(gameData token);
  public int size();

  ArrayList<gameData> listGames();
}
