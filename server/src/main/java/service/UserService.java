package service;

import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;

    public UserService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess){
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    // Finds corresponding UserData given a username. Returns NULL if no user is found.
    public UserData getUser(String username){
        return userDataAccess.findUser(username);
    }


    public AuthData registerUser(UserData userData){
        //If there is no user with the same username
        if(getUser(userData.username()) == null){
            userDataAccess.createUser(userData);
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
            authDataAccess.addAuthData(authData);
            System.out.println("Added authToken " + authToken + " to " + userData.username());
            return authData;
        }
        return null;
    }

}
