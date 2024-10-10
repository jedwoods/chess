package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import spark.*;

public class Server {
    Service service= new Service();


    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.


        Spark.delete("/db", service::clear);
        Spark.post("/user", service::register);
        Spark.post("/game", service::newGame);
        Spark.delete("/session", service::logout);
        Spark.post("/session", service::login);
//        Spark.get("/game", service::listGames);
//        Spark.put("/game",service::joinGame);

        Spark.exception(DataAccessException.class, this::exceptionHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();

    }

    private Object exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.StatusCode());
//        return new Gson().toJson(ex);
        return new Gson().toJson(ex);

    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
