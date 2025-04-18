package ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class ChessClient {

    private final ServerFacade server;
    private final String serverURL;

    private State state = State.PRELOGIN;
    private int activeGameID = 0;

    public ChessClient(String serverURL){
        this.serverURL = serverURL;

        server = new ServerFacade(serverURL);
    }

    public ChessClient(String serverURL, Object object){
        this.serverURL = serverURL;

        server = new ServerFacade(serverURL);
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
                case "legal" -> legal(params);
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
            server.wsConnect(serverURL);
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
            server.wsConnect(serverURL);
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
            activeGameID = Integer.parseInt(params[0]);
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
            activeGameID = Integer.parseInt(params[0]);
            help();
            return 1;
        }

        System.out.println("Please enter valid input or list games and try again");
        return 0;
    }

    public int redraw(){

        if(!server.redrawFacade(activeGameID)){
            System.out.println("Failed to redraw game");
            return 0;
        }
        return 1;
    }

    public int leave(){
        state = State.POSTLOGIN;
        help();
        int result = server.leaveFacade(activeGameID) ? 1 : 0;
        activeGameID = 0;
        return result;
    }

    public int move(String... params){
        if(params.length == 2 || params.length == 3){

            if(params[0].length() > 2 || params[1].length() > 2){
                System.out.println("Please enter valid positions (A8, b4, etc)");
                return 0;
            }

            if (!Character.isAlphabetic(params[0].charAt(0)) || !Character.isAlphabetic(params[1].charAt(0))) {
                System.out.println("Please enter valid positions (A8, b4, etc)");
                return 0;
            }

            if (!Character.isDigit(params[0].charAt(1)) || !Character.isDigit(params[1].charAt(1))) {
                System.out.println("Please enter valid positions (A8, b4, etc)");
                return 0;
            }

            int startColumn = letterToIndex(params[0].charAt(0));
            int endColumn = letterToIndex(params[1].charAt(0));
            int startRow = Integer.parseInt(String.valueOf(params[0].charAt(1)));
            int endRow = Integer.parseInt(String.valueOf(params[1].charAt(1)));

            if(startColumn == 0 || endColumn == 0 || startRow > 8 || startRow <= 0 || endRow > 8 || endRow <= 0){
                System.out.println("Please enter valid positions (A8, b4, etc)");
                return 0;
            }


            ChessPosition start = new ChessPosition(startRow, startColumn);
            ChessPosition end = new ChessPosition(endRow, endColumn);

            ChessPiece.PieceType piece = null;
            if(params.length == 3){
                String promotionPiece = params[2];
                if(Objects.equals(promotionPiece, "knight")){
                    piece = ChessPiece.PieceType.KNIGHT;
                }
                else if(Objects.equals(promotionPiece, "bishop")){
                    piece = ChessPiece.PieceType.BISHOP;
                }
                else if(Objects.equals(promotionPiece, "rook")){
                    piece = ChessPiece.PieceType.ROOK;
                }
                else {
                    piece = ChessPiece.PieceType.QUEEN;

                }
            }


            ChessMove move = new ChessMove(start, end, piece);
            if(!server.moveFacade(activeGameID, move)){
                return 0;
            }
            return 1;
        }

        System.out.println("Please enter move: <START (A2)> <END (A3)>");
        return 0;
    }

    public int resign(){
        System.out.println("Are you sure you want to resign? Please answer YES or NO");

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        var tokens = line.toLowerCase().split(" ");
        var params = Arrays.copyOfRange(tokens, 0, tokens.length);

        if(params.length == 1 && Objects.equals(params[0], "yes")){
            if(!server.resignFacade(activeGameID)){
                System.out.println("Resign failed, please try again");
                return 0;
            }
            System.out.println("Successfully Resigned");
            return 1;
        }
        System.out.println("Resign cancelled");

        return 0;
    }

    public int legal(String... params){
        if(params.length == 1) {

            if (params[0].length() > 2) {
                System.out.println("Please enter valid positions (A8, b4, etc)");
                return 0;
            }

            if (!Character.isAlphabetic(params[0].charAt(0)) || !Character.isDigit(params[0].charAt(1))) {
                System.out.println("Please enter valid positions (A8, b4, etc)");
                return 0;
            }

            int startColumn = letterToIndex(params[0].charAt(0));

            if (startColumn == 0) {
                System.out.println("Please enter valid position (A8, b4, etc)");
            }

            ChessPosition start = new ChessPosition(Integer.parseInt(String.valueOf(params[0].charAt(1))), startColumn);

            server.legalMoves(activeGameID, start);

            return 1;
        }

        System.out.println("Please enter input <POSITION>");

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
                    - move <START> <END> <PROMOTION PIECE> - to move a piece. Please enter a <PROMOTION PIECE>
                                                              upon a pawn promotion.
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