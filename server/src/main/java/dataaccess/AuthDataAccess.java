package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuthDataAccess {

    private final Map<String, String> userDataMap = new HashMap<>();

    public AuthDataAccess(){

    }

    public void addAuthData(AuthData authData) throws DataAccessException {
        userDataMap.put(authData.authToken(), authData.username());
    }

    public boolean removeAuthData(String authToken) throws DataAccessException {
        if(userDataMap.get(authToken) != null){
            userDataMap.remove(authToken);
            return true;
        }
        return false;
    }

    //returns the username
    public String validAuthToken(String authToken) throws DataAccessException {
        return userDataMap.get(authToken);
    }

    public void clear(){
        userDataMap.clear();
    }

    // REGISTRATION:
    // Add AuthData

    // LOGIN:
    // Add AuthData

    // LOGOUT:
    // Remove authToken

    // LIST GAMES:
    // Find authData by authToken

    // CREATE GAME:
    // Find authData by authToken

    // JOIN GAME:
    // Find authData by authToken




}
