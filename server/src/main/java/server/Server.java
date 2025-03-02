package server;

import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;
import service.UserService;
import spark.*;

public class Server {

    UserHandler userHandler;
    UserDataAccess userDataAccess = new UserDataAccess();
    AuthDataAccess authDataAccess = new AuthDataAccess();

    public Server(){
        UserService userService = new UserService(userDataAccess, authDataAccess);
        userHandler = new UserHandler(userService);
    }

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);



        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();




    }

    public int port(){
        return Spark.port();
    }

    public Object clear(Request request, Response response){
        System.out.println("Clear Called");
        response.status(200);
        return "";
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
