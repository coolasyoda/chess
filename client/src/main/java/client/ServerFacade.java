package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import ui.PrintBoard;
import exception.ResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerFacade {

    private final String serverURL;
    private String authToken = null;
    private String userUsername = null;
    Map<Integer, Object> joinedGames = new HashMap<>();
    List<Map<String, Object>> games;

    private WebsocketFacade ws;


    public ServerFacade(String url){
        serverURL = url;
    }

    public boolean registerFacade(String username, String password, String email) {
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);
        request.put("email", email);

        var path = "/user";
        try {
            Map response = this.makeRequest("POST", path, request, Map.class);
            authToken = (String) response.get("authToken");
            userUsername = (String) response.get("username");
            return true;
        }
        catch (ResponseException responseException){
            System.out.println("Username is already taken");
            return false;
        }
    }

    public boolean loginFacade(String username, String password){
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);

        var path = "/session";
        try {
            Map response = this.makeRequest("POST", path, request, Map.class);
            userUsername = (String) response.get("username");
            authToken = (String) response.get("authToken");
            System.out.println("SUCCESSFULLY LOGGED IN");
            return true;
        }
        catch (ResponseException responseException){
            System.out.println("Incorrect password or username");
            return false;
        }
    }

    public boolean logoutFacade(){
        var path = "/session";
        try {
            Map response = this.makeRequest("DELETE", path, null, null);
            authToken = null;
            return true;
        }
        catch (ResponseException responseException){
            return false;
        }
    }

    public int createFacade(String gameName){
        Map<String, String> request = new HashMap<>();
        request.put("gameName", gameName);

        var path = "/game";
        try {
            Map response = this.makeRequest("POST", path, request, Map.class);
            // DO NOT UNDO WEIRD CASTING!
            Integer gameID = ((Number) response.get("gameID")).intValue();
            System.out.println("Created " + gameName + " with ID: " + gameID);
            return gameID;
        }
        catch (ResponseException responseException){
            System.out.println("ERROR CREATING GAME");
            return 0;
        }
    }

    public boolean joinFacade(Number gameID, String color){
        boolean playerType = false;

        Map<String, Object> request = new HashMap<>();
        if(Objects.equals(color, "white")){
            request.put("playerColor", "WHITE");
            playerType = true;
        }
        else if (Objects.equals(color, "black")) {
            request.put("playerColor", "BLACK");
        }
        else{
            return false;
        }

        Map<String, Object> gameToMove = (Map<String, Object>) joinedGames.get(gameID);

        // Already in game
        assert gameToMove != null;
        if(Objects.equals((String) gameToMove.get("whiteUsername"), userUsername)
                || Objects.equals((String) gameToMove.get("blackUsername"), userUsername)){
            redrawFacade((Integer) gameID);

            UserGameCommand command = new ConnectCommand(authToken, (Integer) gameID, color);

            ws.sendCommand(new Gson().toJson(command));

            return true;
        }


        var realGameID = games.get(((Integer) gameID)-1).get("gameID");

        request.put("gameID", realGameID);

        var path = "/game";
        try {
            makeRequest("PUT", path, request, null);
            System.out.println("Joined Game "+ gameID);
            ChessGame game = new ChessGame();
            PrintBoard board = new PrintBoard(game, null);
            board.printBoard(playerType);
            return true;
        }
        catch (ResponseException responseException){
            return false;
        }
    }


    public boolean listFacade(){
        var path = "/game";
        try {
            Map response = this.makeRequest("GET", path, null, Map.class);
            games = (List<Map<String, Object>>) response.get("games");

            joinedGames.clear();

            int gameID = 1;

            for (Map<String, Object> game : games) {

                int readGameID = ((Number) game.get("gameID")).intValue();
                String whiteUsername = (String) game.get("whiteUsername");
                String blackUsername = (String) game.get("blackUsername");
                String gameName = (String) game.get("gameName");

                joinedGames.put(readGameID, game);

                System.out.println("Game ID: " + gameID + ", Name: " + gameName);
                System.out.println("White Username: " + (whiteUsername != null ? whiteUsername : "AVAILABLE"));
                System.out.println("Black Username: " + (blackUsername != null ? blackUsername : "AVAILABLE"));
                System.out.println();
                gameID++;
            }

            return true;
        }
        catch (ResponseException responseException){
            System.out.println("ERROR LISTING GAMES");
            return false;
        }
    }

    public boolean observeFacade(String gameID){

        var maxGames = joinedGames.size();

        if(maxGames == 0 || Integer.parseInt(gameID) >= maxGames + 1){
            return false;
        }

        if(Integer.parseInt(gameID) == 0){
            return false;
        }

        var realGameID = games.get((Integer.parseInt(gameID))-1).get("gameID");

        Map<String, Object> request = new HashMap<>();
        request.put("gameID", gameID);

        var path = "/game/single";
        try {
            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);
            PrintBoard board = new PrintBoard(game, null);
            board.printBoard(true);
            return true;
        }
        catch (ResponseException responseException){
            System.out.println("CATCH");
            return false;
        }

    }

    public boolean moveFacade(Integer gameID, ChessMove move) {
        var maxGames = joinedGames.size();

        if(maxGames == 0 || gameID >= maxGames + 1){
            return false;
        }

        if(gameID == 0 || gameID < 0){
            return false;
        }


        boolean white = true;
        int isInGame = isInGameAsWhite(gameID);

        if(isInGame == -1){
            return false;
        }
        else{
            white = (isInGame == 1);
        }

        var path = "/game/single";
        Map<String, Object> request = new HashMap<>();
        request.put("gameID", gameID);
        try {
            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);
            if( !( (game.getTeamTurn() == ChessGame.TeamColor.WHITE && white)
                    || (game.getTeamTurn() == ChessGame.TeamColor.BLACK && !white) ) ){
                System.out.println("Please wait your turn!");
                return false;
            }

        }
        catch (ResponseException responseException){
            System.out.println("Could not retrieve game!");
            return false;
        }



        path = "/game/move";

        request.clear();
        request.put("gameID", gameID);

        Map<String, Integer> start = new HashMap<>();
        start.put("row", move.getStartPosition().getRow());
        start.put("col", move.getStartPosition().getColumn());

        Map<String, Integer> end = new HashMap<>();
        end.put("row", move.getEndPosition().getRow());
        end.put("col", move.getEndPosition().getColumn());

        Map<String, Object> moveObject = new HashMap<>();
        moveObject.put("start", start);
        moveObject.put("end", end);
        moveObject.put("promotion", move.getPromotionPiece());

        request.put("move", moveObject);

        try{
            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);

            PrintBoard board = new PrintBoard(game, null);
            board.printBoard(white);

            MakeMoveCommand command = new MakeMoveCommand(authToken, (Integer) gameID, move);
            ws.sendCommand(new Gson().toJson(command));

            return true;
        }
        catch (ResponseException e){

        }

        return false;
    }

    public boolean redrawFacade(Integer gameID){

        var maxGames = joinedGames.size();

        if(maxGames == 0 || gameID >= maxGames + 1){
            return false;
        }

        if(gameID == 0){
            return false;
        }

        var realGameID = games.get(gameID-1).get("gameID");

        Map<String, Object> request = new HashMap<>();
        request.put("gameID", gameID);

        boolean white = true;
        int isInGame = isInGameAsWhite(Integer.valueOf(gameID));

        if(isInGame == -1){
            return false;
        }
        else{
            white = (isInGame == 1);
        }

        var path = "/game/single";
        try {
            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);
            PrintBoard board = new PrintBoard(game, null);
            board.printBoard(white);
            return true;
        }
        catch (ResponseException responseException){
            return false;
        }
    }

    public boolean legalMoves(Integer gameID, ChessPosition startPosition){

        var maxGames = joinedGames.size();

        if(maxGames == 0 || gameID >= maxGames + 1){
            return false;
        }

        if(gameID == 0){
            return false;
        }

        var realGameID = games.get(gameID-1).get("gameID");

        Map<String, Object> request = new HashMap<>();
        request.put("gameID", gameID);

        boolean white = true;
        int isInGame = isInGameAsWhite(Integer.valueOf(gameID));

        white = (isInGame != 0);


        var path = "/game/single";
        try {
            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);

            if( !( (game.getTeamTurn() == ChessGame.TeamColor.WHITE && white)
                    || (game.getTeamTurn() == ChessGame.TeamColor.BLACK && !white) ) ){
                System.out.println("Please wait your turn!");
                return false;
            }

            Collection<ChessMove> moves = game.validMoves(startPosition);
            PrintBoard board = new PrintBoard(game, moves);
            board.printBoard(white);
            return true;
        }
        catch (ResponseException responseException){
            System.out.println("CATCH");
            return false;
        }
    }

    public boolean resignFacade(Integer gameID){
        System.out.println("RESIGN FACADE");
        return false;
    }

    public boolean wsConnect(String serverURL){
        try{
            ws = new WebsocketFacade(serverURL);
            return true;
        }
        catch(ResponseException e) {
            System.out.println("Failed to connect to WebSocket");
            return false;
        }
    }


    private int isInGameAsWhite(Integer gameID){

        Map<String, Object> gameToMove = (Map<String, Object>) joinedGames.get(gameID);

        assert gameToMove != null;
        if(Objects.equals((String) gameToMove.get("whiteUsername"), userUsername)){
            return 1;
        }
        else if(Objects.equals((String) gameToMove.get("blackUsername"), userUsername)){
            return 0;
        }
        else{
            return -1;
        }

    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);


            if (authToken != null) {
                http.setRequestProperty("authorization", authToken);
            }

            writeBody(request, http);
            http.connect();

            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}