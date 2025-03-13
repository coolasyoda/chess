package dataaccess;

import chess.ChessGame;
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

        String data = "INSERT INTO games (gameName, game) VALUES (?, ?, ?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(data, Statement.RETURN_GENERATED_KEYS)) {

            statement.setNull(1, java.sql.Types.VARCHAR);
            statement.setNull(2, java.sql.Types.VARCHAR);
            statement.setString(3, game.gameName());
            statement.setString(4, game.chessGame().toString());
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
            String statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=?, WHERE gameID";
            try (var ps = conn.prepareStatement(statement)) {
                String query = "SELECT whiteUsername, blackUsername WHERE gameID=?";
                var results = conn.prepareStatement(query);
                results.setInt(5, gameID);
                var rs = results.executeQuery();


                if(Objects.equals(playerColor, "WHITE") && rs.getString("whiteUsername") == null){
                    ps.setString(1, username);
                }
                else if(rs.getString("blackUsername") == null){
                    ps.setString(2, username);
                }
                ps.setInt(5, gameID);
                if(!(ps.executeUpdate()==0)){
                    throw new DataAccessException("gameID not found");
                }

            }
        } catch (SQLException | DataAccessException e) {
            return null;
        }

        return null;
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
              `game` TEXT,
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
