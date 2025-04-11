package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import chess.ChessGame;
import chess.ChessBoard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    static Map<Session, Integer> gameSessions = new HashMap<>();

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
                leave(session, new Gson().fromJson(message, LeaveCommand.class));
            }
            case "RESIGN" -> {
                resign(session, new Gson().fromJson(message, ResignCommand.class));
            }
        }
    }

    private void connect(Session session, int gameID, String authToken, String player) {
        AuthDataAccess authDataAccess = new AuthDataSQL();
        try {
            System.out.println("CONNECT: " + authDataAccess.validAuthToken(authToken));

            String message;

            if(Objects.equals(player, "observer")){
                message = authDataAccess.validAuthToken(authToken) + " has joined game " + gameID + " as an observer";
            }
            else{
                message = authDataAccess.validAuthToken(authToken) + " has joined game " + gameID + " as " + player;
            }

            gameSessions.put(session, gameID);
            broadcastMessage(session, gameID, message);

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

            ChessGame game = Server.getGameDataAccess().getGame(gameID);

            broadcastMessage(session, gameID, message);
            broadcastGame(session, gameID, game);

        }
        catch (DataAccessException | IOException e){
            System.out.println("Couldn't retrieve username: " + e);
        }
    }

    private void leave(Session session, LeaveCommand command){
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        AuthDataAccess authDataAccess = new AuthDataSQL();
        try {
            System.out.println("LEAVE: " + authDataAccess.validAuthToken(authToken));
            gameSessions.put(session, 0);
            String message = authDataAccess.validAuthToken(authToken) + " has left game " + gameID;
            broadcastMessage(session, gameID, message);
        }
        catch (DataAccessException | IOException e){
            System.out.println("Couldn't retrieve username");
        }
    }

    private void resign(Session session, ResignCommand command){
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        AuthDataAccess authDataAccess = new AuthDataSQL();
        try {
            System.out.println("RESIGN: " + authDataAccess.validAuthToken(authToken));

            String message = authDataAccess.validAuthToken(authToken) + " has resigned game " + gameID + "!";
            broadcastMessage(session, gameID, message);
        }
        catch (DataAccessException | IOException e){
            System.out.println("Couldn't retrieve username");
        }
    }

    public void broadcastMessage(Session session, Integer gameID, String message) throws IOException {
        System.out.println("BROADCASTING MESSAGE: " + message);
        for (Session gameSession : gameSessions.keySet()) {
            if(Objects.equals(gameSessions.get(gameSession), gameID)){
                if(session != gameSession){
                    gameSession.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
                }
            }
        }
    }

    public void broadcastGame(Session session, Integer gameID, ChessGame game) throws IOException {
        System.out.println("BROADCASTING GAME");
        for (Session gameSession : gameSessions.keySet()) {
            if(Objects.equals(gameSessions.get(gameSession), gameID)){
                if(session != gameSession){
                    gameSession.getRemote().sendString(new Gson().toJson(new LoadGameMessage(game)));
                }
            }
        }
    }

}