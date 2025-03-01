package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class AuthDataAccess {

    private final Map<String, AuthData> userDataMap = new HashMap<>();

    public AuthDataAccess(){

    }

    public void addAuthData(AuthData authData){
        userDataMap.put(authData.username(), authData);
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
