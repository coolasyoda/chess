package client;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.PrintBoard;
import ui.ResponseException;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ServerFacade {

    private final String serverURL;
    private String authToken = null;

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
            username = (String) response.get("username");
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
            username = (String) response.get("username");
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

    public boolean createFacade(String gameName){
        Map<String, String> request = new HashMap<>();
        request.put("gameName", gameName);

        var path = "/game";
        try {
            Map response = this.makeRequest("POST", path, request, Map.class);
            // DO NOT UNDO WEIRD CASTING!
            Integer gameID = ((Number) response.get("gameID")).intValue();
            System.out.println("Created " + gameName + " with ID: " + gameID);
            return true;
        }
        catch (ResponseException responseException){
            System.out.println("ERROR CREATING GAME");
            return false;
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

        request.put("gameID", gameID);

        var path = "/game";
        try {
            makeRequest("PUT", path, request, null);
            System.out.println("Joined Game "+ gameID);
            ChessGame game = new ChessGame();
            PrintBoard board = new PrintBoard(game);
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
            List<Map<String, Object>> games = (List<Map<String, Object>>) response.get("games");

            for (Map<String, Object> game : games) {
                Integer gameID = ((Number) game.get("gameID")).intValue();
                String whiteUsername = (String) game.get("whiteUsername");
                String blackUsername = (String) game.get("blackUsername");
                String gameName = (String) game.get("gameName");

                System.out.println("Game ID: " + gameID + ", Name: " + gameName);
                System.out.println("White Username: " + (whiteUsername != null ? whiteUsername : "AVAILABLE"));
                System.out.println("Black Username: " + (blackUsername != null ? blackUsername : "AVAILABLE"));
                System.out.println();
            }

            return true;
        }
        catch (ResponseException responseException){
            System.out.println("ERROR LISTING GAMES");
            return false;
        }
    }

    public boolean observeFacade(String gameID){
        ChessGame game = new ChessGame();
        System.out.println("WHITE BOARD:");
        PrintBoard board = new PrintBoard(game);
        board.printBoard(true);
        System.out.println("BLACK BOARD:");
        board.printBoard(false);

        return false;
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
            http.addRequestProperty("Content-Type", "application/json");
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