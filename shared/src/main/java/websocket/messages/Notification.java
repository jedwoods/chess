package websocket.messages;

import com.google.gson.Gson;

public record Notification(ServerMessage.ServerMessageType type, String message) {



  public String toString() {
    return new Gson().toJson(this);
  }
}
