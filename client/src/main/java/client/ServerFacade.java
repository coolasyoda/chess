package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import ui.PrintBoard;
import exception.ResponseException;
import websocket.commands.*;

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
            listFacade(false);
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

        listFacade(false);
        if(joinedGames.isEmpty()){
            System.out.println("There are no games to join!");
            return false;
        }
        Map<String, Object> gameToMove = (Map<String, Object>) joinedGames.get(gameID);

        // Already in game
        assert gameToMove != null;
        if(Objects.equals((String) gameToMove.get("whiteUsername"), userUsername)
                || Objects.equals((String) gameToMove.get("blackUsername"), userUsername)){

            if(Objects.equals((String) gameToMove.get("blackUsername"), userUsername)){
                ws.setColor(false);
            }

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

            UserGameCommand command = new ConnectCommand(authToken, (Integer) gameID, color);
            ws.sendCommand(new Gson().toJson(command));

            request.clear();
            request.put("gameID", gameID);
            path = "/game/single";

//            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);
//            PrintBoard board = new PrintBoard(game, null);
//            board.printBoard(playerType);
            listFacade(false);

            if(!playerType){
                ws.setColor(false);
            }

            return true;
        }
        catch (ResponseException responseException){
            return false;
        }
    }

    public boolean listFacade(){
        return listFacade(true);
    }

    private boolean listFacade(boolean print){
        var path = "/game";
        try {
            Map response = this.makeRequest("GET", path, null, Map.class);
            games = (List<Map<String, Object>>) response.get("games");

            joinedGames.clear();

            int gameID = 1;

            for (Map<String, Object> game : games) {

                int readGameID = ((Number) game.get("gameID")).intValue();

                if(print){
                    String whiteUsername = (String) game.get("whiteUsername");
                    String blackUsername = (String) game.get("blackUsername");
                    String gameName = (String) game.get("gameName");

                    System.out.println("Game ID: " + gameID + ", Name: " + gameName);
                    System.out.println("White Username: " + (whiteUsername != null ? whiteUsername : "AVAILABLE"));
                    System.out.println("Black Username: " + (blackUsername != null ? blackUsername : "AVAILABLE"));
                    System.out.println();
                }

                joinedGames.put(readGameID, game);
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

        listFacade(false);
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
        //            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);
//            PrintBoard board = new PrintBoard(game, null);
//            board.printBoard(true);
        UserGameCommand command = new ConnectCommand(authToken, Integer.parseInt(gameID), "observer");
        ws.sendCommand(new Gson().toJson(command));
        return true;

    }

    public boolean moveFacade(Integer gameID, ChessMove move) {
        var maxGames = joinedGames.size();

        if(maxGames == 0 || gameID >= maxGames + 1 || gameID == 0 || gameID < 0){
            System.out.println("Issue with gameID");
            return false;
        }

        boolean black;
        int isInGame = isInGameAsBlack(gameID);

        if(isInGame == -1){
            System.out.println("Cannot move piece unless joined in the game!");
            return false;
        }
        else{
            black = (isInGame == 1);
        }

        var path = "/game/single";
        Map<String, Object> request = new HashMap<>();
        request.put("gameID", gameID);
        try {
            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);

            if(game.isOver()){
                System.out.println("Game is over");
                return false;
            }

            if( !( (game.getTeamTurn() == ChessGame.TeamColor.WHITE && !black)
                    || (game.getTeamTurn() == ChessGame.TeamColor.BLACK && black) ) ){
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

        //            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);

//            ChessGame.TeamColor color = game.getBoard().getPiece(move.getEndPosition()).getTeamColor();
//            ChessGame.TeamColor oppositeColor = null;
//
//            if(color == ChessGame.TeamColor.WHITE){
//                oppositeColor = ChessGame.TeamColor.BLACK;
//            }
//            else {
//                oppositeColor = ChessGame.TeamColor.WHITE;
//            }

//            PrintBoard board = new PrintBoard(game, null);
//            board.printBoard(!black);

        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
        ws.sendCommand(new Gson().toJson(command));

        return true;

    }

    public boolean redrawFacade(Integer gameID){

        var maxGames = joinedGames.size();

        if(maxGames == 0 || gameID >= maxGames + 1 || gameID == 0){
            System.out.println("Issue with gameID");
            return false;
        }

        var realGameID = games.get(gameID-1).get("gameID");

        Map<String, Object> request = new HashMap<>();
        request.put("gameID", gameID);

        boolean black = (isInGameAsBlack(Integer.valueOf(gameID)) == 1);

        var path = "/game/single";
        try {
            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);
            PrintBoard board = new PrintBoard(game, null);
            board.printBoard(!black);
            return true;
        }
        catch (ResponseException responseException){
            return false;
        }
    }

    public void legalMoves(Integer gameID, ChessPosition startPosition){
        var maxGames = joinedGames.size();
        if(maxGames == 0 || gameID >= maxGames + 1 || gameID == 0){
            System.out.println("Could not retrieve legal moves");
            return;
        }
        var realGameID = games.get(gameID-1).get("gameID");
        Map<String, Object> request = new HashMap<>();
        request.put("gameID", gameID);
        boolean black = false;
        int isInGame = isInGameAsBlack(Integer.valueOf(gameID));
        black = (isInGame == 1);
        var path = "/game/single";
        try {
            ChessGame game = this.makeRequest("PUT", path, request, ChessGame.class);
            if(game.isOver()){
                System.out.println("Game is over");
                return;
            }
            Collection<ChessMove> moves = game.validMoves(startPosition);
            if(moves.isEmpty()){
                System.out.println("No legal moves for selected piece");
                return;
            }
            PrintBoard board = new PrintBoard(game, moves);
            board.printBoard(!black);
        }
        catch (ResponseException responseException){
            System.out.println("LEGAL MOVES CATCH");
        }
    }
    public boolean resignFacade(Integer gameID){
        ResignCommand command = new ResignCommand(authToken, gameID);
        ws.sendCommand(new Gson().toJson(command));
        return true;
    }
    public boolean leaveFacade(Integer gameID){
        LeaveCommand command = new LeaveCommand(authToken, gameID);
        ws.sendCommand(new Gson().toJson(command));
        ws.setColor(true);
        return true;
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
    private int isInGameAsBlack(Integer gameID){
        Map<String, Object> gameToMove = (Map<String, Object>) joinedGames.get(gameID);
        assert gameToMove != null;
        if(Objects.equals((String) gameToMove.get("whiteUsername"), userUsername)){
            return 0;
        }
        else if(Objects.equals((String) gameToMove.get("blackUsername"), userUsername)){
            return 1;
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
//500 lines is the limit, which is why there is little whitespace legalMoves onward :(