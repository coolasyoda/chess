package websocket.commands;

public class MakeMoveCommand extends UserGameCommand{

    public MakeMoveCommand(String authToken, int gameID){
        super(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
    }
}
