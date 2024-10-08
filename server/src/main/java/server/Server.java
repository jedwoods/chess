package server;
import spark.*;


public class Server {
    handler currentHandler = new handler();

    public int run(int desiredPort) {



        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.


        Spark.delete("/db",this::clearDB );

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clearDB (Request req, Response res) {
        this.currentHandler.clear();
        return res;
//        return new Gson().tojson()Map.;
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
