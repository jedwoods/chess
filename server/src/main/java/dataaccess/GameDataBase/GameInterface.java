package dataaccess.GameDataBase;

public interface GameInterface {
  public void remove(Integer currentID);
  public gameData get(Integer currentID);
  public void add(gameData token);
  public int size();
}
