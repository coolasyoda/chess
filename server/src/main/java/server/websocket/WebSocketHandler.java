package server.websocket;

import com.google.gson.Gson;
//import dataaccess.DataAccess;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
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
            case "CONNECT" -> System.out.println("CONNECT");
            case "MAKE_MOVE" -> System.out.println("MAKE_MOVE");
            case "LEAVE" -> System.out.println("LEAVE");
            case "RESIGN" -> System.out.println("RESIGN");
        }
    }

    private void connect(){

    }

    private void makeMove(){

    }

    private void leave(){

    }

    private void resign(){

    }

}