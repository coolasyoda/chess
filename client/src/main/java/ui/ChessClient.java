package ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
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
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> quit();
                default -> help();
            };
        }
        else if(state == State.GAMEPLAY){
            return switch (cmd){
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "legal" -> legal();
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
            help();
            return 1;
        }

        System.out.println("Please enter valid login");

        return 0;
    }

    public int logout(){
        boolean logoutVal = server.logoutFacade();
        if(logoutVal){
            System.out.println("LOGOUT SUCCESSFUL");
            state = State.PRELOGIN;
            help();
        }
        else {
            System.out.println("LOGOUT FAILED");
        }

        return logoutVal ? 1 : 0;
    }

    public int create(String... params){
        if(params.length == 1){
            int gameID = server.createFacade(params[0]);
            if(gameID == 0){
                System.out.println("Error Creating Game");
                return 0;
            }
            return gameID;
        }

        System.out.println("Please enter valid game name");

        return 0;
    }

    public int join(String... params){

        if(params.length == 2){
            for (int i = 0; i < params[0].length(); i++) {
                if (!Character.isDigit(params[0].charAt(i))) {
                    System.out.println("Please enter valid input");
                    return 0;
                }
            }

            if(!server.joinFacade(Integer.parseInt(params[0]), params[1])){
                System.out.println("Failed to join game");
                return 0;
            }
            state = State.GAMEPLAY;
            help();
            return 1;
        }

        System.out.println("Please enter valid input");
        return 0;
    }

    public int list(){
        return server.listFacade() ? 1 : 0;
    }

    public int observe(String... params){

        if(params.length == 1){
            for (int i = 0; i < params[0].length(); i++) {
                if (!Character.isDigit(params[0].charAt(i))) {
                    System.out.println("Please enter valid input");
                    return 0;
                }
            }

            if(!server.observeFacade(params[0])){
                System.out.println("Failed to observe game");
                return 0;
            }
            state = State.GAMEPLAY;
            return 1;
        }

        System.out.println("Please enter valid input or list games and try again");
        return 0;
    }

    public int redraw(){
        return 0;
    }

    public int leave(){
        return 0;
    }

    public int move(String... params){
        if(params.length == 3 || params.length == 4){
            for (int i = 0; i < params[0].length(); i++) {
                if (!Character.isDigit(params[0].charAt(i))) {
                    System.out.println("Please enter valid gameID");
                    return 0;
                }
            }

            if(params[1].length() > 2 || params[2].length() > 2){
                System.out.println("Please enter valid positions (A8, b4, etc)");
                return 0;
            }

            if (!Character.isAlphabetic(params[1].charAt(0)) || !Character.isAlphabetic(params[2].charAt(0))) {
                System.out.println("Please enter valid positions (A8, b4, etc)");
                return 0;
            }

            if (!Character.isDigit(params[1].charAt(1)) || !Character.isDigit(params[2].charAt(1))) {
                System.out.println("Please enter valid positions (A8, b4, etc)");
                return 0;
            }

            int start_column = letterToIndex(params[1].charAt(0));
            int end_column = letterToIndex(params[2].charAt(0));

            if(start_column == 0 || end_column == 0){
                System.out.println("Please enter valid positions (A8, b4, etc)");
            }


            ChessPosition start = new ChessPosition(params[1].charAt(1), start_column);
            ChessPosition end = new ChessPosition(params[2].charAt(1), end_column);

            ChessMove move = new ChessMove(start, end, null);



            var test = server.moveFacade(params[0], move);
            System.out.println("SUCCESS");
        }



        System.out.println("Please enter move: <GAME ID> <START (A2)> <END (A3)>");


        return 0;
    }

    public int resign(){
        return 0;
    }

    public int legal(){
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
        } else if (state == State.GAMEPLAY) {
            System.out.println("""
                    - redraw - to redraw the chess board
                    - leave - leave the game
                    - move <START> <END> <PROMOTION PIECE> - to move a piece. You can leave <PROMOTION PIECE> empty
                                                              unless you want to specify a piece for pawn promotion.
                    - resign - to forfeit the match
                    - legal <START> - to highlight legal moves
                    - help
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

    private int letterToIndex(Character character){
        if(character == 'a' || character == 'A'){
            return 1;
        }
        if(character == 'b' || character == 'B'){
            return 2;
        }
        if(character == 'c' || character == 'C'){
            return 3;
        }
        if(character == 'd' || character == 'D'){
            return 4;
        }
        if(character == 'e' || character == 'E'){
            return 5;
        }
        if(character == 'f' || character == 'F'){
            return 6;
        }
        if(character == 'g' || character == 'G'){
            return 7;
        }
        if(character == 'h' || character == 'h'){
            return 8;
        }
        return 0;
    }

}