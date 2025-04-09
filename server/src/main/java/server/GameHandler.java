package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

public class GameHandler {

    private static final Gson GSON = new Gson();
    GameService gameService;


    public GameHandler(GameService gameService) {
       this.gameService = gameService;
    }


    public Object createGame(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");
        GameData game = GSON.fromJson(request.body(), GameData.class);

        if(gameService.validateUser(authToken) == null){
            response.status(401);
            return GSON.toJson(Map.of("message", "Error: unauthorized"));
        }

        game = gameService.createGame(game);

        if(game == null){
            response.status(400);
            return GSON.toJson(Map.of("message", "Error: bad request"));
        }

        response.status(200);
        return GSON.toJson(game);
    }

    public Object joinGame(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");

        JsonObject gameRequest = GSON.fromJson(request.body(), JsonObject.class);

        if(!gameRequest.has("playerColor") || !gameRequest.has("gameID")){
            response.status(400);
            return GSON.toJson(Map.of("message", "Error: bad request"));
        }

        System.out.println("Raw Request Body: " + request.body());

        String playerColor = gameRequest.get("playerColor").getAsString();
        int gameID = gameRequest.get("gameID").getAsInt();

        String username = gameService.validateUser(authToken);

        if(!(playerColor.equals("WHITE") || playerColor.equals("BLACK"))){
            response.status(400);
            return GSON.toJson(Map.of("message", "Error: bad request"));
        }

        if(username == null){
            response.status(401);
            return GSON.toJson(Map.of("message", "Error: unauthorized"));
        }

        GameData joinedGame = gameService.joinGame(gameID, username, playerColor);

        if(joinedGame == null){
            response.status(403);
            return GSON.toJson(Map.of("message", "Error: already taken"));
        }

        response.status(200);
        return "";
    }

    public Object listGames(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");

        if(gameService.validateUser(authToken) == null){
            response.status(401);
            return GSON.toJson(Map.of("message", "Error: unauthorized"));
        }

        List<GameData> games = gameService.listGames(authToken);
        Map<String, List<GameData>> gamesList = Map.of("games", games);

        System.out.println(games.toString());

        response.status(200);
        return GSON.toJson(gamesList);
    }

    public Object makeMove(Request request, Response response) throws DataAccessException {
        System.out.println("makeMove GameHandler");

        String authToken = request.headers("authorization");

        if(gameService.validateUser(authToken) == null){
            response.status(401);
            return GSON.toJson(Map.of("message", "Error: unauthorized"));
        }

        System.out.println("RAW REQUEST BODY: " + request.body());

        Map<String, Object> moveBody = GSON.fromJson(request.body(), Map.class);

        Integer gameID = ((Number) moveBody.get("gameID")).intValue();

        Map<String, Object> moveMap = (Map<String, Object>) moveBody.get("move");

        Map<String, Integer> startMap = (Map<String, Integer>) moveMap.get("start");
        Map<String, Integer> endMap = (Map<String, Integer>) moveMap.get("end");


        int startRow = ((Number) startMap.get("row")).intValue();
        int startCol = ((Number) startMap.get("col")).intValue();
        int endRow = ((Number) endMap.get("row")).intValue();
        int endCol = ((Number) endMap.get("col")).intValue();

        ChessPosition start = new ChessPosition(startRow, startCol);
        ChessPosition end = new ChessPosition(endRow, endCol);

        ChessPiece.PieceType promotion = null;
        if (moveMap.containsKey("promotion")) {
            //CONVERT TO ACTUAL PIECE
            //promotion = (String) moveMap.get("promotion");
        }

        ChessMove move = new ChessMove(start, end, null);


        gameService.makeMove(gameID, move);

        response.status(200);
        return GSON.toJson(Map.of("message", "Move successful"));
    }


}
