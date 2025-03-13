package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import passoff.model.TestAuthResult;
import service.GameService;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.List;
import java.util.UUID;

public class ServiceTests {

    UserService userService;
    GameService gameService;

    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;
    GameDataAccess gameDataAccess;

    @BeforeEach
    public void setup() throws DataAccessException {
//        userDataAccess = new UserDataAccess();
//        authDataAccess = new AuthDataAccess();
//        gameDataAccess = new GameDataAccess();

        userDataAccess = new UserDataSQL();
        authDataAccess = new AuthDataSQL();
        gameDataAccess = new GameDataSQL();

        userService = new UserService(userDataAccess, authDataAccess);
        gameService = new GameService(gameDataAccess, authDataAccess);

        userDataAccess.clear();
        gameDataAccess.clear();
        authDataAccess.clear();


    }

    @Test
    public void posTestRegisterUser() throws DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "");
        Assertions.assertNotNull(userService.registerUser(userData), "Register Failed");
    }

    @Test
    public void negTestRegisterUser() throws DataAccessException {
        UserData userData = new UserData("", "", "");
        Assertions.assertNull(userService.registerUser(userData), "Register accepted null values");
    }

    @Test
    public void posTestGetUser() throws DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "");
        userService.registerUser(userData);
        Assertions.assertEquals(userData, userService.getUser("testUser"),"Did not retrieve user");
    }

    @Test
    public void negTestGetUser() throws DataAccessException {
        Assertions.assertNull(userService.getUser("testUser"), "User was never registered");
    }

    @Test
    public void posTestLoginUser() throws DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "");
        userService.registerUser(userData);
        Assertions.assertNotNull(userService.loginUser(userData), "Returned null on good request");
    }

    @Test
    public void negTestLoginUser() throws DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "");
        userService.registerUser(userData);

        UserData noUser = new UserData("noUser", "testPassword", "");
        UserData badPass = new UserData("testUser", "badPassword", "");

        Assertions.assertNull(userService.loginUser(noUser), "User was never registered");
        Assertions.assertNull(userService.loginUser(badPass), "Wrong password accepted");
    }

    @Test
    public void posTestLogoutUser() throws DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "");
        userService.registerUser(userData);
        AuthData autData = userService.loginUser(userData);
        Assertions.assertTrue(userService.logoutUser(autData.authToken()), "Response should be true");
    }

    @Test
    public void negTestLogoutUser() throws DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "");
        userService.registerUser(userData);
        AuthData autData = userService.loginUser(userData);

        String badToken = UUID.randomUUID().toString();
        Assertions.assertFalse(userService.logoutUser(badToken), "Response should be false");
        Assertions.assertFalse(userService.logoutUser(""), "Response should be false");
        Assertions.assertFalse(userService.logoutUser(null), "Response should be false");

    }

    @Test
    public void posTestCreateGame() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(null, null, null, "testGame", game);
        GameData createdGame = gameService.createGame(gameData);
        Assertions.assertNotNull(createdGame, "Should return game data");
        Assertions.assertNotNull(createdGame.gameID(), "Should have gameID");
    }

    @Test
    public void negTestCreateGame() throws DataAccessException {
        GameData gameData1 = new GameData(null, null, null, null, null);
        GameData gameData2 = new GameData(null, null, null, "", null);
        Assertions.assertNull(gameService.createGame(gameData1), "Should return null on null gameName");
        Assertions.assertNull(gameService.createGame(gameData2), "Should return null on empty name");
    }

    @Test
    public void posTestValidateUser() throws DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "");
        AuthData authData = userService.registerUser(userData);
        Assertions.assertEquals("testUser", gameService.validateUser(authData.authToken()), "Did not return username");

    }

    @Test
    public void negTestValidateUser() throws DataAccessException {
        String badToken = UUID.randomUUID().toString();
        Assertions.assertNull(gameService.validateUser(badToken), "Should return null with a badToken");
        Assertions.assertNull(gameService.validateUser(""), "Should return null with an empty");
    }

    @Test
    public void posListGamesTest() throws DataAccessException {

        List<GameData> gamesList;
        List<GameData> testList = new java.util.ArrayList<>(List.of());

        UserData userData = new UserData("testUser", "testPassword", "");
        AuthData authData = userService.registerUser(userData);

        //Test with no games
        gamesList = gameService.listGames(authData.authToken());
        Assertions.assertTrue(gamesList.isEmpty(), "Games List should be empty");

        UserData userData1 = new UserData("testUser1", "testPassword", "");
        userService.registerUser(userData1);
        UserData userData2 = new UserData("testUser2", "testPassword", "");
        userService.registerUser(userData2);

        GameData gameData1 = new GameData(null, null, null, "testGame1", null);
        GameData createdGame1 = gameService.createGame(gameData1);

        testList.add(createdGame1);

        //Test with 1 game
        gamesList = gameService.listGames(authData.authToken());
        Assertions.assertEquals(testList, gamesList, "Games List should only have testGame1");

        gameData1 = gameService.joinGame(createdGame1.gameID(), "testUser1", "WHITE");
        gameData1 = gameService.joinGame(createdGame1.gameID(), "testUser2", "BLACK");
        testList.clear();
        GameData testData = new GameData(1, "testUser1", "testUser2", "testGame1", null);
        testList.add(testData);

        //Test with joined users
        gamesList = gameService.listGames(authData.authToken());
        Assertions.assertEquals(testList, gamesList, "Games should have joined users");


        GameData gameData2 = new GameData(null, null, null, "testGame2", null);
        GameData createdGame2 = gameService.createGame(gameData2);

        testList.add(createdGame2);

        //Test with 2 games
        gamesList = gameService.listGames(authData.authToken());
        Assertions.assertEquals(testList, gamesList, "Games List should have testGame1 and testGame2");

    }

    @Test
    public void negListGamesTest() throws DataAccessException {
        String badToken = UUID.randomUUID().toString();

        Assertions.assertNull(gameService.listGames(badToken));

    }

    @Test
    public void posTestJoinGame() throws DataAccessException {
        UserData userData1 = new UserData("testUser1", "testPassword", "");
        userService.registerUser(userData1);
        UserData userData2 = new UserData("testUser2", "testPassword", "");
        userService.registerUser(userData2);

        GameData gameData = new GameData(null, null, null, "testGame", null);
        GameData createdGame = gameService.createGame(gameData);

        gameData = gameService.joinGame(createdGame.gameID(), "testUser1", "WHITE");
        gameData = gameService.joinGame(createdGame.gameID(), "testUser2", "BLACK");

        GameData testData = new GameData(1, "testUser1", "testUser2", "testGame", null);
        Assertions.assertEquals(testData, gameData, "EXPECTED VALUES: White=testUser1 Black=testUser2");
    }

    @Test
    public void negTestJoinGame() throws DataAccessException {
        UserData userData1 = new UserData("testUser1", "testPassword", "");
        userService.registerUser(userData1);
        UserData userData2 = new UserData("testUser2", "testPassword", "");
        userService.registerUser(userData2);

        ChessGame game = new ChessGame();

        GameData gameData = new GameData(null, null, null, "testGame", game);
        GameData createdGame = gameService.createGame(gameData);

        gameData = gameService.joinGame(createdGame.gameID(), "testUser1", "WHITE");

        //Color already joined
        Assertions.assertNull(gameService.joinGame(createdGame.gameID(), "testUser2", "WHITE"));

        //Game does not exist
        Assertions.assertNull(gameService.joinGame(10, "testUser2", "BLACK"));

        //Wrong color
        Assertions.assertNull(gameService.joinGame(createdGame.gameID(), "testUser2", "GREEN"));

        //Empty username
        Assertions.assertNull(gameService.joinGame(createdGame.gameID(), "", "BLACK"));
    }


}
