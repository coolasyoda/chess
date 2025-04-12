package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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

    public WebSocketHandler() {
        super();
    }

    static Map<Session, Integer> gameSessions = new HashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        gameSessions.put(session, 0);
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
                    connect(session,    jsonObject.get("gameID").getAsInt(),
                            jsonObject.get("authToken").getAsString(),
                            null    );
                }
                else{
                    connect(session,    jsonObject.get("gameID").getAsInt(),
                            jsonObject.get("authToken").getAsString(),
                            jsonObject.get("stringParam").getAsString()    );
                }

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
            default -> {
                error(session, new Throwable("Invalid Command"));
            }
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) throws Exception {
        System.out.println("EXIT WS" + session.toString());
        gameSessions.remove(session);
    }

    private boolean connect(Session session, int gameID, String authToken, String player) {
        AuthDataAccess authDataAccess = new AuthDataSQL();
        try {
            System.out.println("CONNECT: " + authDataAccess.validAuthToken(authToken));

            GameDataAccess gameDataAccess = new GameDataSQL();

            if(gameDataAccess.getGame(gameID) == null){
                error(session, new Throwable("Invalid game ID"));
                return false;
            }

            if(authDataAccess.validAuthToken(authToken) == null){
                error(session, new Throwable("Invalid authToken"));
                return false;
            }

            String message;

            if(player == null){
                message = "message white " + gameID;
            }
            else if(Objects.equals(player, "observer")){
                message = authDataAccess.validAuthToken(authToken) + " has joined game " + gameID + " as an observer";
            }
            else{
                message = authDataAccess.validAuthToken(authToken) + " has joined game " + gameID + " as " + player;
            }

            gameSessions.put(session, gameID);
            broadcastMessage(session, gameID, message, false);
            broadcastGame(session, gameID, new ChessGame(), true);



        }
        catch (DataAccessException | IOException e){
            System.out.println("Couldn't retrieve username");
        }

        return true;

    }

    private void makeMove(Session session, MakeMoveCommand command){
        String authToken = command.getAuthToken();
        ChessMove move = command.getMove();
        Integer gameID = command.getGameID();

        AuthDataAccess authDataAccess = new AuthDataSQL();
        try {
            System.out.println("MAKE MOVE: " + authDataAccess.validAuthToken(authToken) + " " + move.toString());

            String message = authDataAccess.validAuthToken(authToken) + " made move " + move + " in game " + gameID;

            GameDataAccess gameDataAccess = new GameDataSQL();

            ChessGame game = gameDataAccess.getGame(gameID);


            if(game.isOver()){
                error(session, new Throwable("Game is over!"));
                return;
            }

            ChessGame.TeamColor color = gameDataAccess.getUserColor(gameID, authDataAccess.validAuthToken(authToken));

            System.out.println("COLOR = " + color + " TEAM TURN = " + game.getTeamTurn());

            if(color != game.getTeamTurn()){
                error(session, new Throwable("Not your turn!"));
                return;
            }

            game = gameDataAccess.makeMove(gameID, move);

            if(game == null){
                error(session, new Throwable("Invalid move!"));
                return;
            }

            broadcastGame(session, gameID, game, false);
            broadcastGame(session, gameID, game, true);
            broadcastMessage(session, gameID, message, false);

            System.out.println("COLOR: " + color.toString());
            ChessGame.TeamColor oppositeColor = null;

            if(color == ChessGame.TeamColor.WHITE){
                oppositeColor = ChessGame.TeamColor.BLACK;
            }
            else {
                oppositeColor = ChessGame.TeamColor.WHITE;
            }

            if(game.isInCheckmate(oppositeColor)){
                message = "Checkmate! " + authDataAccess.validAuthToken(authToken) + " won game " + gameID;

            }
            else if(game.isInCheck(oppositeColor)){
                message = "Check!";

            }
            else if(game.isInStalemate(oppositeColor)){
                message = authDataAccess.validAuthToken(authToken) + "'s move resulted in a stalemate in game " + gameID;
            }
            else {
                return;
            }

            broadcastMessage(session, gameID, message, true);
            broadcastMessage(session, gameID, message, false);

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
            broadcastMessage(session, gameID, message, false);
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

            GameDataAccess gameDataAccess = new GameDataSQL();
            ChessGame.TeamColor color = gameDataAccess.getUserColor(gameID, authDataAccess.validAuthToken(authToken));

            if(color == null){
                error(session, new Throwable("Cannot resign in a game you are observing!"));
                return;
            }

            if(gameDataAccess.getGame(gameID).isOver()){
                error(session, new Throwable("Cannot resign in a game that has already ended!"));
                return;
            }

            gameDataAccess.resign(gameID, authDataAccess.validAuthToken(authToken));

            String message = authDataAccess.validAuthToken(authToken) + " has resigned game " + gameID + "!";
            broadcastMessage(session, gameID, message, false);
            broadcastMessage(session, gameID, message, true);
        }
        catch (DataAccessException | IOException e){
            System.out.println("Couldn't retrieve username");
        }
    }

    @OnWebSocketError
    public void error(Session session, Throwable cause) throws IOException {
        session.getRemote().sendString(new Gson().toJson(new ErrorMessage("ERROR: " + cause.getMessage())));
    }

    public void broadcastMessage(Session session, Integer gameID, String message, boolean self) throws IOException {
        System.out.println("BROADCASTING MESSAGE: " + message);
        if(self){
            session.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
        }
        else{
            for (Session gameSession : gameSessions.keySet()) {
                if(Objects.equals(gameSessions.get(gameSession), gameID)){
                    if(session != gameSession){
                        gameSession.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
                    }
                }
            }
        }
    }

    public void broadcastGame(Session session, Integer gameID, ChessGame game, boolean self) throws IOException {
        System.out.println("BROADCASTING GAME");
        if(self){
            session.getRemote().sendString(new Gson().toJson(new LoadGameMessage(game)));
        }
        else{
            for (Session gameSession : gameSessions.keySet()) {
                if(Objects.equals(gameSessions.get(gameSession), gameID)){
                    if(session != gameSession){
                        gameSession.getRemote().sendString(new Gson().toJson(new LoadGameMessage(game)));
                    }
                }
            }
        }
    }

}