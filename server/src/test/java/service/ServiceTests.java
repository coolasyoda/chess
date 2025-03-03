package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import passoff.model.TestAuthResult;
import service.GameService;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.UUID;

public class ServiceTests {

    UserService userService;
    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;
    GameDataAccess gameDataAccess;

    @BeforeEach
    public void setup() {
        userDataAccess = new UserDataAccess();
        authDataAccess = new AuthDataAccess();
        gameDataAccess = new GameDataAccess();

        userService = new UserService(userDataAccess, authDataAccess);

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
        Assertions.assertEquals(userService.getUser("testUser"), userData, "Did not retrieve user");
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

    //createGame
    //validateUser
    //joinGame
    //listGames







}
