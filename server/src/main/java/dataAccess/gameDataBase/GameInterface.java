package dataAccess.gameDataBase;

import java.util.ArrayList;

public interface GameInterface {
  public void remove(Integer currentID);
  public GameData get(Integer currentID);
  public void add(GameData token);
  public int size();

  ArrayList<GameData> listGames();
}
