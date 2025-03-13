package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Map;

public class UserDataAccess {

    private final Map<String, UserData> userDataMap = new HashMap<>();

    public UserDataAccess(){

    }

    public UserData findUser(String username) throws DataAccessException {
        if(userDataMap.containsKey(username)){
            return userDataMap.get(username);
        }
        return null;
    }

    public void createUser(UserData user) throws DataAccessException {
        if(findUser(user.username()) == null){
            userDataMap.put(user.username(), user);
        }
    }

    public void clear() throws DataAccessException {
        userDataMap.clear();
    }



    // REGISTRATION:
    // Find UserData by Username
    // Add UserData
    // Add Authdata

    // LOGIN:
    // Find Userdata by Username
    // Add AuthData

    // LOGOUT:
    // Find authData by authToken
    // Remove authToken

}
