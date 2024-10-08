package dataaccess.GameDataBase;

import java.util.ArrayList;
import java.util.Objects;

public class GameDataBase implements GameInterface {
  ArrayList<gameData> gameStorage;

  public GameDataBase(){
    gameStorage = new ArrayList<>();
  }

  @Override
  public void remove(Integer currentID) {
    var gameObj = this.get(currentID);
    if (gameObj != null){
      gameStorage.remove(gameObj);
    }
  }

  @Override
  public gameData get(Integer currentID) {
    for (var gameObj : gameStorage){
      if (Objects.equals(gameObj.gameID(), currentID)){
        gameStorage.remove(gameObj);
        return gameObj;
      }
    }
    return null;
  }

  @Override
  public void add(gameData token) {
    gameStorage.add(token);
  }

  @Override
  public int size() {
    return gameStorage.size();
  }

}
