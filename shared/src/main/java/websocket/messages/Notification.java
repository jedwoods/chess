package websocket.messages;

public class Notification extends ServerMessage{
  private final String messsage;

  public Notification(ServerMessageType type, String messsage) {
    super(type);
    this.messsage=messsage;
  }

  public String getMesssage(){
    return this.messsage;
  }
}
