package client;

import chess.ChessGame;
import ui.PrintBoard;

import java.util.Objects;

public class ServerFacade {

    private final String serverURL;
    private String authToken;

    public ServerFacade(String url){
        serverURL = url;
    }

    public boolean register(String username, String password, String email){

        System.out.println(password);


        if(Objects.equals(password, "pass")){
            return true;
        }

        return false;
    }

    public boolean login(String username, String password){
        ChessGame game = new ChessGame();
        PrintBoard board = new PrintBoard(game);


        return false;
    }

    public boolean logout(){
        return false;
    }

    public boolean create(String gameName){
        return false;
    }

    public boolean join(String ID, String color){
        return false;
    }

    public boolean list(){
        return false;
    }

    public boolean observe(String ID){
        return false;
    }

}
