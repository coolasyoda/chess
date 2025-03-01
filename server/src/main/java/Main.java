import chess.*;
//import dataaccess.MemoryDataAccess;
//import dataaccess.MySqlDataAccess;
//import service.PetService;

public class Main {
    public static void main(String[] args) {
        try {
            var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            System.out.println("♕ 240 Chess Server: " + piece);
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

//            DataAccess dataAccess = new UserDataAccess();
//            if (args.length >= 2 && args[1].equals("sql")) {
//                dataAccess = new MySqlDataAccess();
//            }

//            var service = new Service(dataAccess);
//            var server = new Server(service).run(port);
//            port = server.port();
//            System.out.printf("Server started on port %d with %s%n", port, dataAccess.getClass());
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("""
                Pet Server:
                java ServerMain <port> [sql]
                """);
    }
}