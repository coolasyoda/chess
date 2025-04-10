package websocket.commands;

public class ConnectCommand extends UserGameCommand{

    public ConnectCommand(String authToken, int gameID, String player){
        super(CommandType.CONNECT, authToken, gameID, player);
    }
}
