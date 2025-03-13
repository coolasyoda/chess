package server;

import dataaccess.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    UserHandler userHandler;
    GameHandler gameHandler;

    boolean useSQL = true;

    UserDataAccess userDataAccess = new UserDataAccess();
    AuthDataAccess authDataAccess = new AuthDataAccess();
    GameDataAccess gameDataAccess = new GameDataAccess();

    UserDataAccess userDataAccessSQL = new UserDataSQL();
    AuthDataAccess authDataAccessSQL = new AuthDataSQL();
    GameDataAccess gameDataAccessSQL = new GameDataSQL();

    public Server(){
        UserService userService;
        GameService gameService;

        if(useSQL){
            userService = new UserService(userDataAccessSQL, authDataAccessSQL);
            gameService = new GameService(gameDataAccessSQL, authDataAccessSQL);
        }
        else{
            userService = new UserService(userDataAccess, authDataAccess);
            gameService = new GameService(gameDataAccess, authDataAccess);
        }

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);
    }

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);
        Spark.get("/game", gameHandler::listGames);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();




    }

    public int port(){
        return Spark.port();
    }

    public Object clear(Request request, Response response) throws DataAccessException {
        System.out.println("Clear Called");
        userDataAccess.clear();
        authDataAccess.clear();
        gameDataAccess.clear();
        response.status(200);
        return "";
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
