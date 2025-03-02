package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import model.GameData;

public class GameService {

    GameDataAccess gameDataAccess;
    AuthDataAccess authDataAccess;

    public GameService(GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public GameData createGame(GameData game){
        return gameDataAccess.newGame(game);
    }

    public String validateUser(String authToken){
        return authDataAccess.validAuthToken(authToken);
    }

    public GameData joinGame(Integer gameID, String username, String playerColor) {
        return gameDataAccess.joinGame(gameID, username, playerColor);
    }
}
