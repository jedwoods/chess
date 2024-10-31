package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import spark.*;

public class Server {
    Handler handler= new Handler();


    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.

        Spark.get("/game", handler::listGames);
        Spark.delete("/db", handler::clear);
        Spark.post("/user", handler::register);
        Spark.post("/game", handler::newGame);
        Spark.delete("/session", handler::logout);
        Spark.post("/session", handler::login);
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

}
