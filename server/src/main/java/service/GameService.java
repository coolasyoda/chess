package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.AuthData;
import model.GameData;

import java.util.List;
import java.util.Objects;

public class GameService {

    GameDataAccess gameDataAccess;
    AuthDataAccess authDataAccess;

    public GameService(GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public GameData createGame(GameData game) throws DataAccessException {
        return gameDataAccess.newGame(game);
    }

    public String validateUser(String authToken){
        return authDataAccess.validAuthToken(authToken);
    }

    public GameData joinGame(Integer gameID, String username, String playerColor) {
        if(!Objects.equals(playerColor, "WHITE") && !Objects.equals(playerColor, "BLACK") || username.isEmpty()){
            return null;
        }
        return gameDataAccess.joinGame(gameID, username, playerColor);
    }

    public List<GameData> listGames(String authToken){
        System.out.println("TEST 2");
        if(validateUser(authToken) == null){
            return null;
        }
        return gameDataAccess.listGames();
    }
}
