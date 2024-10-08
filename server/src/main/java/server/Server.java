package server;
import dataaccess.Service;
import dataaccess.UserDataBase.user;
import spark.*;
import com.google.gson.*;

public class Server {
    Handler handler = new Handler();


    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.


        Spark.delete("/db", handler::clear);
        Spark.post("/user", handler::register);
        Spark.post("/game", handler::newGame);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
