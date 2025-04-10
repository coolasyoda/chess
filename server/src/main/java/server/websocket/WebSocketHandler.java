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


import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        System.out.println("CONNECT WS" + session.toString());
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

        System.out.println("Received command: " + message);
        switch (jsonObject.get("commandType").getAsString()) {
            case "CONNECT" -> {

                if(jsonObject.get("observer") == null){
                    System.out.println("OBSERVER IS NULL");
                    return;
                }

                connect( jsonObject.get("gameID").getAsInt(),
                                            jsonObject.get("authToken").getAsString(),
                                            jsonObject.get("observer").getAsBoolean()    );

            }
            case "MAKE_MOVE" -> makeMove();
            case "LEAVE" -> leave();
            case "RESIGN" -> resign();
        }
    }

    private void connect(int gameID, String authToken, boolean observe) {
        AuthDataAccess authDataAccess = new AuthDataSQL();
        try {
            System.out.println("CONNECT: " + authDataAccess.validAuthToken(authToken));
        }
        catch (DataAccessException e){
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