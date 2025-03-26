import chess.*;
import ui.REPL;

import java.util.Spliterator;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);


        var serverURL = "http://localhost:8080";
        new REPL(serverURL).run();
    }
}