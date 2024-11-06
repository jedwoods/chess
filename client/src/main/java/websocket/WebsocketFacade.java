package websocket;



import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.glassfish.tyrus.core.wsadl.model.Endpoint;


import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//@WebSocket
public class WebsocketFacade extends Endpoint {


  public WebsocketFacade(){

  }

  public void onMessage() {

  }

  public void send(String msg) throws Exception {}

  public void onOpen(Session session, EndpointConfig endpointConfig) {
  }


}
