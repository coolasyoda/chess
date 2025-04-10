package websocket.commands;

public class LeaveCommand extends UserGameCommand{

    public LeaveCommand(String authToken, int gameID){
        super(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
    }
}
