package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameDataAccessSQLTest {

    GameDataSQL gameDataAccess;

    @BeforeEach
    public void setup(){
        gameDataAccess = new GameDataSQL();
        gameDataAccess.clear();
    }


    @Test
    public void posNewGame() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(null, null, null, "testGame", game);

        GameData newGame1 = gameDataAccess.newGame(gameData);
        Assertions.assertNotNull(newGame1, "Game should be returned");
        Assertions.assertEquals(gameData.gameName(), newGame1.gameName(), "Names should be equals");
        Assertions.assertEquals(gameData.chessGame(), newGame1.chessGame(), "Games should be equal");
        Assertions.assertEquals(1, newGame1.gameID(), "Game ID should be 1");
        GameData newGame2 = gameDataAccess.newGame(gameData);
        Assertions.assertEquals(2, newGame2.gameID(), "Game ID should be 2");

    }

    @Test
    public void negNewGame() throws DataAccessException {
        GameData newGame = gameDataAccess.newGame(null);
        Assertions.assertNull(newGame, "Game should be null");

        GameData gameData = new GameData(null, null, null, null, null);
        newGame = gameDataAccess.newGame(gameData);
        Assertions.assertNull(newGame, "Game needs name and chessGame");

    }

    @Test
    public void posJoinGame() {

    }

    @Test
    public void negJoinGame() {

    }

    @Test
    public void posListGames() {

    }

    @Test
    public void negListGames() {

    }


}
