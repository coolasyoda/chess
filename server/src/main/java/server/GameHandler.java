package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.AuthDataAccess;
import model.GameData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class GameHandler {

    private static final Gson gson = new Gson();
    GameService gameService;


    public GameHandler(GameService gameService) {
       this.gameService = gameService;
    }


    public Object createGame(Request request, Response response) {
        String authToken = request.headers("authorization");
        GameData game = gson.fromJson(request.body(), GameData.class);

        if(gameService.validateUser(authToken) == null){
            response.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        }

        game = gameService.createGame(game);

        if(game == null){
            response.status(400);
            return gson.toJson(Map.of("message", "Error: bad request"));
        }

        response.status(200);
        return gson.toJson(game);
    }

    public Object joinGame(Request request, Response response) {
        String authToken = request.headers("authorization");

        JsonObject gameRequest = gson.fromJson(request.body(), JsonObject.class);
        String playerColor = gameRequest.get("playerColor").getAsString();
        int gameID = gameRequest.get("gameID").getAsInt();

        String username = gameService.validateUser(authToken);

        if(username == null){
            response.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        }

        GameData joinedGame = gameService.joinGame(gameID, username, playerColor);

        if(joinedGame == null){
            response.status(403);
            return gson.toJson(Map.of("message", "Error: already taken"));
        }

        response.status(200);
        return "";
    }


    // LIST GAMES:
    // games(ListRequest)

    // CREATE GAME:
    // games(NewRequest)

    // JOIN GAME:
    // games(JoinRequest)


}
