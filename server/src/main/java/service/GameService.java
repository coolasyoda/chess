package service;

import chess.ChessGame;
import chess.ChessMove;
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

    public String validateUser(String authToken) throws DataAccessException {
        return authDataAccess.validAuthToken(authToken);
    }

    public GameData joinGame(Integer gameID, String username, String playerColor) {
        if(!Objects.equals(playerColor, "WHITE") && !Objects.equals(playerColor, "BLACK") || username.isEmpty()){
            return null;
        }
        return gameDataAccess.joinGame(gameID, username, playerColor);
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        if(validateUser(authToken) == null){
            return null;
        }
        return gameDataAccess.listGames();
    }

    public ChessGame makeMove(Integer gameID, ChessMove move){
        System.out.println("Service makeMove");
        return gameDataAccess.makeMove(gameID, move);
    }

    public ChessGame getGame(Integer gameID){
        System.out.println("Service getGame");
        return gameDataAccess.getGame(gameID);
    }

}
