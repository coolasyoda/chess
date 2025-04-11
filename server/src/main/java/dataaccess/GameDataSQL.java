package dataaccess;

import chess.*;
import com.google.gson.Gson;
import model.GameData;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class GameDataSQL extends GameDataAccess{

    public GameDataSQL() {
        try{
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public GameData newGame(GameData game) throws DataAccessException {
        System.out.println("NEW GAME\n");
        if(game == null || game.gameName() == null || game.gameName().isEmpty()){
            return null;
        }

        String data = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(data, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, "");
            statement.setString(2, "");
            statement.setString(3, game.gameName());
            statement.setString(4, new Gson().toJson(game));
            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int gameID = generatedKeys.getInt(1);
                    GameData newGame = new GameData(gameID, null, null, game.gameName(), game.chessGame());
                    System.out.println("Creating Game: " + gameID);
                    return newGame;
                }
            }

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        return null;
    }

    public GameData joinGame(Integer gameID, String username, String playerColor){
        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try(var results = conn.prepareStatement(query)){
                results.setInt(1, gameID);
                try (var rs = results.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("gameID not found");
                    }
                    var white = rs.getString("whiteUsername");
                    var black = rs.getString("blackUsername");
                    var gameName = rs.getString("gameName");
                    var game = new Gson().fromJson(rs.getString("game"), ChessGame.class);

                    if(Objects.equals(playerColor, "WHITE") && white.isEmpty()){
                        String update = "UPDATE games SET whiteUsername=? WHERE gameID=?";
                        var ps = conn.prepareStatement(update);
                        ps.setString(1, username);
                        ps.setInt(2, gameID);
                        ps.executeUpdate();
                        white = username;

                    }
                    else if(Objects.equals(playerColor, "BLACK") && black.isEmpty()){
                        String update = "UPDATE games SET blackUsername=? WHERE gameID=?";
                        var ps = conn.prepareStatement(update);
                        ps.setString(1, username);
                        ps.setInt(2, gameID);
                        ps.executeUpdate();
                        black = username;

                    }
                    else{
                        return null;
                    }

                    return new GameData(gameID, white, black, gameName, game);
                }

            }

        } catch (SQLException | DataAccessException e) {
            return null;
        }
    }

    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try(var results = conn.prepareStatement(query);
                var rs = results.executeQuery()) {
                    while (rs.next()){
                        int gameID = rs.getInt("gameID");
                        String white = rs.getString("whiteUsername");
                        String black = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");

                        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);

                        if(Objects.equals(white, "")){
                            white = null;
                        }
                        if(Objects.equals(black, "")){
                            black = null;
                        }

                        games.add(new GameData(gameID, white, black, gameName, game));
                    }
            }
        }
        catch (SQLException | DataAccessException e){
            throw new DataAccessException("Error listing games: " + e);
        }

        return games;
    }

    public ChessGame makeMove(Integer gameID, ChessMove move){
        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT gameID, game FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(query)) {
                ps.setInt(1, gameID);

                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);

                        game.makeMove(move);
                        System.out.println(game.getBoard().boardToString(true));

                        // Update the game in the database
                        String update = "UPDATE games SET game=? WHERE gameID=?";
                        var updatePs = conn.prepareStatement(update);
                        updatePs.setString(1, new Gson().toJson(game));
                        updatePs.setInt(2, gameID);
                        updatePs.executeUpdate();

                        return game;
                    } else {
                        throw new DataAccessException("Game not found");
                    }
                }
            }
        } catch (SQLException | DataAccessException e ) {
            System.out.println("MOVE CATCH");
            return null;
        }
        catch (InvalidMoveException moveException){
            System.out.println("INVALID MOVE");
            return null;
        }
    }

    public ChessGame getGame(Integer gameID){
        System.out.println("service getGame");
        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT gameID, game FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(query)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Gson().fromJson(rs.getString("game"), ChessGame.class);
                    } else {
                        throw new DataAccessException("Game not found");
                    }
                }
            }
        } catch (SQLException | DataAccessException e ) {
            System.out.println("GET GAME CATCH");
            return null;
        }
    }

    public boolean resign(int gameID, String username) {

        System.out.println("SQL RESIGN");

        return false;
    }

    public void clear(){
        System.out.println("GAME CLEAR\n");

        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE games ")) {
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            return;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(256),
              `blackUsername` VARCHAR(256),
              `gameName` VARCHAR(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(`whiteUsername`),
              INDEX(`blackUsername`),
              INDEX(`gameName`)
            )"""
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException ex){
            throw new DataAccessException("Database did not connect");
        }
    }



}
