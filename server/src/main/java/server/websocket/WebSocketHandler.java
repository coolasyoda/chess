package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.AuthDataAccess;
import dataaccess.AuthDataSQL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;


import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        System.out.println("CONNECT WS" + session.toString());
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

        System.out.println("Received command: " + message);
        switch (jsonObject.get("commandType").getAsString()) {
            case "CONNECT" -> {

                if(jsonObject.get("stringParam") == null){
                    System.out.println("PLAYER IS NULL");
                    return;
                }

                connect(session,    jsonObject.get("gameID").getAsInt(),
                                    jsonObject.get("authToken").getAsString(),
                                    jsonObject.get("stringParam").getAsString()    );

            }
            case "MAKE_MOVE" -> {

                makeMove(session, new Gson().fromJson(message, MakeMoveCommand.class));

            }
            case "LEAVE" -> {
                leave(session, jsonObject.get("gameID").getAsInt(), jsonObject.get("authToken").getAsString());
            }
            case "RESIGN" -> {
                resign(session, jsonObject.get("gameID").getAsInt(), jsonObject.get("authToken").getAsString());
            }
        }
    }

    private void connect(Session session, int gameID, String authToken, String player) {
        AuthDataAccess authDataAccess = new AuthDataSQL();
        try {
            System.out.println("CONNECT: " + authDataAccess.validAuthToken(authToken));

            String message = authDataAccess.validAuthToken(authToken) + " has joined game " + gameID + " as " + player;

            session.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
        }
        catch (DataAccessException | IOException e){
            System.out.println("Couldn't retrieve username");
        }

    }

    private void makeMove(Session session, MakeMoveCommand command){
        String authToken = command.getAuthToken();
        ChessMove move = command.getMove();
        Integer gameID = command.getGameID();

        AuthDataAccess authDataAccess = new AuthDataSQL();
        try {
            System.out.println("MAKE MOVE: " + authDataAccess.validAuthToken(authToken) + " " + move.toString());

            String message = authDataAccess.validAuthToken(authToken) + " made move " + move + " in game " + gameID;

            session.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
//            session.getRemote().sendString(new Gson().toJson(new LoadGameMessage()));

        }
        catch (DataAccessException | IOException e){
            System.out.println("Couldn't retrieve username");
        }
    }

    private void leave(Session session, int gameID, String authToken){
        AuthDataAccess authDataAccess = new AuthDataSQL();
        try {
            System.out.println("LEAVE: " + authDataAccess.validAuthToken(authToken));

            String message = authDataAccess.validAuthToken(authToken) + " has left game " + gameID;

            session.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
        }
        catch (DataAccessException | IOException e){
            System.out.println("Couldn't retrieve username");
        }
    }

    private void resign(Session session, int gameID, String authToken){
        AuthDataAccess authDataAccess = new AuthDataSQL();
        try {
            System.out.println("RESIGN: " + authDataAccess.validAuthToken(authToken));

            String message = authDataAccess.validAuthToken(authToken) + " has resigned game " + gameID + "!";

            session.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
        }
        catch (DataAccessException | IOException e){
            System.out.println("Couldn't retrieve username");
        }
    }




}