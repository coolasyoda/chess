package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;

    public UserService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess){
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    // Finds corresponding UserData given a username. Returns NULL if no user is found.
    public UserData getUser(String username) throws DataAccessException {
        if(username.isEmpty()){
            throw new DataAccessException("username is null");
        }
        return userDataAccess.findUser(username);
    }


    public AuthData registerUser(UserData userData) throws DataAccessException {
        try {
            //If there is no user with the same username
            if(getUser(userData.username()) == null){
                userDataAccess.createUser(userData);
                return generateAuthData(userData);
            }
            return null;
        } catch (DataAccessException e){
            return null;
        }

    }

    public AuthData loginUser(UserData userData) throws DataAccessException {

        UserData potentialLogin = userDataAccess.findUser(userData.username());
        // Check if the passwords are the same
        if(potentialLogin != null && Objects.equals(potentialLogin.password(), userData.password())){
            return generateAuthData(userData);
        }

        return null;
    }

    public boolean logoutUser(String authToken){
        return authDataAccess.removeAuthData(authToken);
    }


    private AuthData generateAuthData(UserData userData){
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        authDataAccess.addAuthData(authData);
        System.out.println("Added authToken " + authToken + " to " + userData.username());
        return authData;
    }



}
