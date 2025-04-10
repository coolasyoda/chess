package ui;

import client.ServerFacade;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class REPL {

    ServerFacade serverFacade;
    private final ChessClient client;

    public REPL(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 chess. Type Help to get started. ♕");
        client.help();

        Scanner scanner = new Scanner(System.in);
        var result = 0;
        while (result != -1) {
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

}
