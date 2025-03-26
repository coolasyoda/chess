package ui;

import client.ServerFacade;

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
        if(state == State.POSTLOGIN){
            return switch (cmd) {
                case "create" -> create(params);
                case "join" -> join(params);
                case "list" -> list();
                case "observe" -> observe();
                case "logout" -> logout();
                case "quit" -> quit();
                default -> help();
            };
        }
        else{
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

//            authToken = register(username, password, email);

            if(!server.registerFacade(username, password, email)){
                System.out.println("Register Failed");
                return 0;
            }

            System.out.println("Successfully Registered:");
            state = State.POSTLOGIN;
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

            if(!server.loginFacade(username, password)){
                System.out.println("Login Failed");
                return 0;
            }
            state = State.POSTLOGIN;

            return 1;
        }

        return 0;
    }

    public int logout(){
        boolean logoutVal = server.logoutFacade();
        if(logoutVal){
            System.out.println("LOGOUT SUCCESSFUL");
            state = State.PRELOGIN;

        }
        else {
            System.out.println("LOGOUT FAILED");
        }

        return logoutVal ? 1 : 0;
    }

    public int create(String... params){
        if(params.length == 1){
            if(!server.createFacade(params[0])){
                System.out.println("Error Creating Game");
                return 0;
            }
            return 1;
        }

        System.out.println("Please enter valid game name");

        return 0;
    }

    public int join(String... params){
        System.out.println("JOIN");

        if(params.length == 2){
            if(!server.joinFacade(Integer.parseInt(params[0]), params[1])){
                System.out.println("Failed to join game");
            }
            return 1;
        }

        return 0;
    }

    public int list(){
        server.listFacade();

        return 0;
    }

    public int observe(String... params){
        server.observeFacade("params[0]");

        return 0;
    }

    public int quit(){
        logout();
        return -1;
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
                - logout
                - help
                - quit
                """);

        return 1;
    }

}