package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{

    private ChessMove move;

    public MakeMoveCommand(String authToken, int gameID, ChessMove move){
        super(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move.toString());
        this.move = move;
    }

    public ChessMove getMove(){
        return move;
    }

}
