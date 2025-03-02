package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameDataAccess {
    // LIST GAMES
    // Get Games

    // CREATE GAME
    // Get gameData by gameName
    // Add gameData

    // JOIN GAME
    // Update gameData by gameID

    private final Map<Integer, GameData> gameDataMap = new HashMap<>();
    // Increments with each new game. NEVER DECREMENTS!
    int numGames = 1;

    public GameData newGame(GameData game) {
        GameData newGame = new GameData(numGames, null, null, game.gameName(), null);
        gameDataMap.put(numGames, newGame);
        numGames++;
        return newGame;
    }

    public GameData joinGame(Integer gameID, String username, String playerColor){
        GameData joinGame = gameDataMap.get(gameID);
        String whiteUsername = joinGame.whiteUsername();
        String blackUsername = joinGame.blackUsername();
        String gameName = joinGame.gameName();
        ChessGame chessGame = joinGame.chessGame();


        if(Objects.equals(playerColor, "WHITE")){
            if(whiteUsername != null){
                return null;
            }
            else{
                whiteUsername = username;
            }
        }

        if(Objects.equals(playerColor, "BLACK")){
            if(blackUsername != null){
                return null;
            }
            else{
                blackUsername = username;
            }
        }

        // To add we have to create a new game, so we need to remove the old game
        gameDataMap.remove(gameID);

        GameData joinedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);

        gameDataMap.put(gameID, joinedGame);

        return joinedGame;
    }


    public void clear(){
        gameDataMap.clear();
    }

}
