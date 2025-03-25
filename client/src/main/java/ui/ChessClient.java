package ui;

import client.ServerFacade;

public class ChessClient {

    private final ServerFacade server;
    private final String serverURL;

    public ChessClient(String serverURL){
        server = new ServerFacade(serverURL);
        this.serverURL = serverURL;


    }

}
