package client;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.PrintBoard;
import ui.ResponseException;

import java.io.*;
import java.net.*;
import java.util.HashMap;
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

    public boolean joinFacade(String ID, String color){

        return false;
    }

    public boolean listFacade(){
        return false;
    }

    public boolean observeFacade(String ID){
        ChessGame game = new ChessGame();
        PrintBoard board = new PrintBoard(game);
        board.printBoard(true);
        System.out.println();
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
            System.out.println("TEST");
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