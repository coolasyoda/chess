package websocket.commands;

public class ConnectCommand extends UserGameCommand{

    public ConnectCommand(String authToken, int gameID, boolean observer){
        super(CommandType.CONNECT, authToken, gameID, observer);
    }
}
