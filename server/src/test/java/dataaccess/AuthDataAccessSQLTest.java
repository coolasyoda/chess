package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class AuthDataAccessSQLTest {

    AuthDataSQL authDataAccess;

    @BeforeEach
    public void setup(){
        authDataAccess = new AuthDataSQL();
        authDataAccess.clear();
    }

    @Test
    public void posRemoveAuthData() throws DataAccessException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), "testUser");
        authDataAccess.addAuthData(authData);

        Assertions.assertTrue(authDataAccess.removeAuthData(authData.authToken()), "Auth token should be removed");

    }

    @Test
    public void negRemoveAuthData() throws DataAccessException {
        Assertions.assertFalse(authDataAccess.removeAuthData(UUID.randomUUID().toString()), "No users present");
        AuthData authData = new AuthData(UUID.randomUUID().toString(), "testUser");
        authDataAccess.addAuthData(authData);
        Assertions.assertFalse(authDataAccess.removeAuthData(UUID.randomUUID().toString()),
                "Random AuthToken should not remove users");
        Assertions.assertFalse(authDataAccess.removeAuthData(null),
                "Empty token should return false");

    }

    @Test
    public void posValidAuthToken() throws DataAccessException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), "testUser");
        authDataAccess.addAuthData(authData);
        Assertions.assertEquals(authData.username(), authDataAccess.validAuthToken(authData.authToken()),
                "User should be present");

    }

    @Test
    public void negValidAuthToken() throws DataAccessException {
        Assertions.assertNull(authDataAccess.validAuthToken(UUID.randomUUID().toString()),
                "No user exists");

        AuthData authData = new AuthData(UUID.randomUUID().toString(), "testUser");
        authDataAccess.addAuthData(authData);

        Assertions.assertNull(authDataAccess.validAuthToken(UUID.randomUUID().toString()),
                "Should not validate authToken when false");

        Assertions.assertNull(authDataAccess.validAuthToken(null),
                "AuthToken is null");
    }

//    addAuthData -> currently returns null NEED TO ADD



}
