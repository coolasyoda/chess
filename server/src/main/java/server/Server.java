package server;

import dataaccess.*;
import server.websocket.WebSocketHandler;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    UserHandler userHandler;
    GameHandler gameHandler;

    boolean useSQL = true;

    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;
    static GameDataAccess gameDataAccess;

    public Server(){
        UserService userService;
        GameService gameService;

        if(useSQL){
            userDataAccess = new UserDataSQL();
            authDataAccess = new AuthDataSQL();
            gameDataAccess = new GameDataSQL();

            userService = new UserService(userDataAccess, authDataAccess);
            gameService = new GameService(gameDataAccess, authDataAccess);
        }
        else{
            userDataAccess = new UserDataAccess();
            authDataAccess = new AuthDataAccess();
            gameDataAccess = new GameDataAccess();

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
        Spark.webSocket("/ws", WebSocketHandler.class);

        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);
        Spark.get("/game", gameHandler::listGames);
        Spark.put("/game/move", gameHandler::makeMove);
        Spark.put("/game/single", gameHandler::getGame);
        Spark.put("/game/resign", gameHandler::resign);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();

    }

    public static GameDataAccess getGameDataAccess(){
        return gameDataAccess;
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
