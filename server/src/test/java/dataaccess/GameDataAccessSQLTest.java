package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
        Assertions.assertNull(newGame, "Game needs name");

    }

    @Test
    public void posJoinGame() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(null, null, null, "testGame", game);
        GameData newGame = gameDataAccess.newGame(gameData);

        GameData gameTest = new GameData(null, "user1", "user2", "testGame", game);

        gameDataAccess.joinGame(newGame.gameID(), "user1", "WHITE");
        newGame = gameDataAccess.joinGame(newGame.gameID(), "user2", "BLACK");

        Assertions.assertEquals(gameTest, newGame, "Users should be added");

    }

    @Test
    public void negJoinGame() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(null, null, null, "testGame", game);
        GameData newGame = gameDataAccess.newGame(gameData);
        int gameID = newGame.gameID();

        GameData gameTest = new GameData(null, "user1", "user2", "testGame", game);

        newGame = gameDataAccess.joinGame(gameID, "user1", "GREEN");

        Assertions.assertNull(newGame, "Must be WHITE or BLACK");

        gameDataAccess.joinGame(gameID, "user1", "WHITE");
        newGame = gameDataAccess.joinGame(gameID, "user2", "WHITE");

        Assertions.assertNull(newGame, "Player position already occupied");

    }

    @Test
    public void makeMove() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(null, null, null, "testGame", game);
        GameData newGame = gameDataAccess.newGame(gameData);

        GameData gameTest = new GameData(null, "user1", "user2", "testGame", game);

        gameDataAccess.joinGame(newGame.gameID(), "user1", "WHITE");
        newGame = gameDataAccess.joinGame(newGame.gameID(), "user2", "BLACK");
        System.out.println(newGame.chessGame().getBoard().toString(true));

        ChessMove move = new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null);

        ChessGame movedGame = gameDataAccess.makeMove(newGame.gameID(), move);


    }


}
