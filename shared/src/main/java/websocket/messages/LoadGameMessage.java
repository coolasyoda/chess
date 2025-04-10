package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage {
    private ChessGame game;

    public LoadGameMessage(ChessGame game){
        this.game = game;
    }

    public ChessGame getGame(){
        return game;
    }

}
