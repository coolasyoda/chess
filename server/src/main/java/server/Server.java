package server;

import spark.*;

public class Server {

    public Server(){

    }

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        //Spark.register("/register", UserHandler::register);




        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();




    }

    public int port(){
        return Spark.port();
    }

    public Object clear(Request req, Response res){
        System.out.println("Clear Called");
        res.status(200);
        return "";
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
