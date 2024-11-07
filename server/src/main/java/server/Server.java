package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.websocket.WebSocketHandler;

import org.eclipse.jetty.websocket.api.Session;
import spark.*;

import java.io.IOException;

@WebSocket
public class Server {
    Handler handler= new Handler();
    public void clear(){
        handler.service.clear();
    }

    private final WebSocketHandler webSocketHandler = new WebSocketHandler(handler.service);


    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/ws", Server.class);
//        Spark.webSocket("/ws", WebSocketHandler.class);

        Spark.delete("/db", handler::clear);
        Spark.post("/user", handler::register);
        Spark.post("/game", handler::newGame);
        Spark.delete("/session", handler::logout);
        Spark.post("/session", handler::login);
        Spark.get("/game", handler::listGames);
        Spark.put("/game", handler::joinGame);

        Spark.exception(DataAccessException.class, this::exceptionHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();

    }

    private Object exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.statusCode());
//        return new Gson().toJson(ex);
        return new Gson().toJson(ex);

    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        webSocketHandler.onMessage(message, session);
    }

}
