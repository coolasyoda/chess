package server.websocket;

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
import websocket.commands.UserGameCommand;
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

                if(jsonObject.get("player") == null){
                    System.out.println("PLAYER IS NULL");
                    return;
                }

                connect(session,    jsonObject.get("gameID").getAsInt(),
                                    jsonObject.get("authToken").getAsString(),
                                    jsonObject.get("player").getAsString()    );

            }
            case "MAKE_MOVE" -> makeMove();
            case "LEAVE" -> leave();
            case "RESIGN" -> resign();
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

    private void makeMove(){
        System.out.println("MAKE_MOVE");
    }

    private void leave(){
        System.out.println("LEAVE");
    }

    private void resign(){
        System.out.println("RESIGN");
    }




}