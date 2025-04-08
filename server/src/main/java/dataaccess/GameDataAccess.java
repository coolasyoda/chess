package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.List;
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
    int numGames;

    public GameDataAccess(){
        numGames = 1;
    }

    public GameData newGame(GameData game) throws DataAccessException {
        if(game.gameName() == null || game.gameName().isEmpty()){
            return null;
        }
        GameData newGame = new GameData(numGames, null, null, game.gameName(), null);
        gameDataMap.put(numGames, newGame);
        System.out.println("Creating Game: " + numGames);
        numGames++;
        return newGame;
    }

    public GameData joinGame(Integer gameID, String username, String playerColor){
        if(gameDataMap.get(gameID) == null){
            return null;
        }

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
                System.out.println("Adding \"" + username + "\" as white player to game: " + gameID);
            }
        }

        if(Objects.equals(playerColor, "BLACK")){
            if(blackUsername != null){
                return null;
            }
            else{
                blackUsername = username;
                System.out.println("Adding \"" + username + "\" as black player to game: " + gameID);
            }
        }

        // To add we have to create a new game, so we need to remove the old game
        gameDataMap.remove(gameID);

        GameData joinedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);

        gameDataMap.put(gameID, joinedGame);

        return joinedGame;
    }

    public List<GameData> listGames() throws DataAccessException {
        return List.copyOf(gameDataMap.values());
    }

    public ChessGame makeMove(Integer gameID, ChessMove move){
        System.out.println("makeMove only supported in SQL");
        return null;
    }

    public void clear(){
        numGames = 1;
        gameDataMap.clear();
    }


}
