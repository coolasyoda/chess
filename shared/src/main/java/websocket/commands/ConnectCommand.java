package websocket.commands;

public class ConnectCommand extends UserGameCommand{

    public ConnectCommand(String authToken, int gameID){
        super(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
    }
}
