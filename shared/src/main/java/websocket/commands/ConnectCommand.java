package websocket.commands;

public class ConnectCommand extends UserGameCommand{

    public ConnectCommand(String authToken, int gameID, String stringParam){
        super(CommandType.CONNECT, authToken, gameID, stringParam);
    }
}
