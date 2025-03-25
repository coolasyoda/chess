package ui;

import client.ServerFacade;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.sql.SQLOutput;
import java.util.Arrays;

public class ChessClient {

    private final ServerFacade server;
    private final String serverURL;

    private State state = State.PRELOGIN;

    public ChessClient(String serverURL){
        server = new ServerFacade(serverURL);
        this.serverURL = serverURL;


    }

    public int eval(String input) {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(state == State.GAMEPLAY){
                System.out.println("GAMEPLAY: ");
                return switch (cmd) {
                    case "create" -> create(params);
                    case "join" -> join(params);
                    case "list" -> list();
                    case "observe" -> observe();
                    case "logout" -> logout();
                    case "quit" -> -1;
                    default -> help();
                };
            }
            else{
                System.out.println("PRE-LOGIN: ");

                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> -1;
                    default -> help();
                };
            }
    }

    public int register(String... params){
        if (params.length == 2 || params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = (params.length == 3) ? params[2] : null;

            if(!server.register(username, password, email)){
                System.out.println("Register Failed");
                return 0;
            }

            System.out.println("Successfully Registered:");
            state = State.GAMEPLAY;
            help();
            return 1;
        }
        System.out.println("Please enter valid registration");
        return 0;
    }

    public int login(String... params){

        if(params.length == 2){
            String username = params[0];
            String password = params[1];

            if(!server.login(username, password)){
                System.out.println("Login Failed");
                return 0;
            }

            return 1;
        }

        return 0;
    }

    public int logout(){
        System.out.println("LOGOUT");

        return server.logout() ? 1 : 0;
    }

    public int create(String... params){
        System.out.println("CREATE");
        if(params.length == 1){
            if(!server.create(params[0])){
                System.out.println("Error Creating Game");
                return 0;
            }
        }

        return 0;
    }

    public int join(String... params){
        System.out.println("JOIN");

        if(params.length == 2){
            if(!server.join(params[0], params[1])){
                System.out.println("Failed to join game");
            }
        }

        return 0;
    }

    public int list(){
        return 0;
    }

    public int observe(String... params){
        System.out.println("OBSERVE");

        if(params.length == 1){
            if(!server.observe(params[0])){
                System.out.println("Failed to observe game");
            }
        }

        return 0;
    }

    public int help(){
        if (state == State.PRELOGIN) {
            System.out.println("""
                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    - login <USERNAME> <PASSWORD> - to login with an existing account
                    - help
                    - quit
                    """);
            return 1;
        }
        System.out.println("""
                - create <NAME> - create a game
                - list - list existing games
                - join <ID> [WHITE | BLACK] - join an existing game
                - observe <ID> - observe a game
                - help
                - quit
                """);

        return 1;
    }

}
