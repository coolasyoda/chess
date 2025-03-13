package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDataAccessSQLTest {

    UserDataAccess userDataAccess;
    UserData userData = new UserData("testUser", "testPassword", "");


    @BeforeEach
    public void setup() throws DataAccessException {
        userDataAccess = new UserDataSQL();
        userDataAccess.clear();
    }

    @Test
    public void posCreateUser() throws DataAccessException {
        userDataAccess.createUser(userData);
        //currently returns null -> NEED TO ADD

    }

    @Test
    public void negCreateUser(){
        //currently returns null -> NEED TO ADD

    }

    @Test
    public void posFindUser() throws DataAccessException {
        userDataAccess.createUser(userData);
        UserData foundUser = userDataAccess.findUser(userData.username());
        Assertions.assertEquals(foundUser.username(), userData.username(), "User not found");

    }

    @Test
    public void negFindUser() throws DataAccessException {
        UserData foundUser = userDataAccess.findUser(userData.username());
        Assertions.assertNull(foundUser, "No users added");

        userDataAccess.createUser(userData);
        foundUser = userDataAccess.findUser("wrongUser");
        Assertions.assertNull(foundUser, "Wrong User found");
    }

    @Test
    public void posVerifyUser() throws DataAccessException {
        //currently returns null -> NEED TO ADD
    }

    @Test
    public void negVerifyUser(){
        //currently returns null -> NEED TO ADD
    }

    @Test
    public void clear(){

    }




}
