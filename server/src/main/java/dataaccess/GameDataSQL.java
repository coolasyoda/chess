package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
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

    public GameData newGame(GameData game) {
        return null;
    }

    public GameData joinGame(Integer gameID, String username, String playerColor){
        return null;
    }

    public List<GameData> listGames() {
        return null;
    }

    public void clear(){

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(256) NOT NULL,
              `blackUsername` VARCHAR(256) NOT NULL,
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
