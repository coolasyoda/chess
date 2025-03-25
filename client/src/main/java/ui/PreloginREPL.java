package ui;

import client.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreloginREPL {

    ServerFacade serverFacade;
    private final ChessClient client;

    public PreloginREPL(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 chess. Type Help to get started. ♕");
//        System.out.print(client.help());
//
//        Scanner scanner = new Scanner(System.in);
//        var result = "";
//        while (!result.equals("quit")) {
//            printPrompt();
//            String line = scanner.nextLine();
//
//            try {
//                result = client.eval(line);
//                System.out.print(BLUE + result);
//            } catch (Throwable e) {
//                var msg = e.toString();
//                System.out.print(msg);
//            }
//        }
        System.out.println();
    }

    private void printPrompt() {
//        System.out.print("\n" + RESET + ">>> " + GREEN);
    }

}
