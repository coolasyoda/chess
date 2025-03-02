package dataaccess;

import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

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


    public void clear(){
        gameDataMap.clear();
    }


}
