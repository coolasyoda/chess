package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        if(game.gameName() == null || game.gameName().isEmpty()){
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
                    GameData newGame = new GameData(gameID, null, null, game.gameName(), null);
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
                        try (var ps = conn.prepareStatement(update)) {
                            ps.setString(1, username);
                            ps.setInt(2, gameID);
                            ps.executeUpdate();
                            white = username;
                        }
                    }
                    else if(Objects.equals(playerColor, "BLACK") && black.isEmpty()){
                        String update = "UPDATE games SET blackUsername=? WHERE gameID=?";
                        try (var ps = conn.prepareStatement(update)) {
                            ps.setString(1, username);
                            ps.setInt(2, gameID);
                            ps.executeUpdate();
                            black = username;
                        }
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

    public List<GameData> listGames() {
        return null;
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
