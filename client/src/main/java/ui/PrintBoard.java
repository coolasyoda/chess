package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;

public class PrintBoard {

    ChessGame game;
    List<ChessPosition> positions = new ArrayList<>();

    public PrintBoard(ChessGame game, Collection<ChessMove> moves){
        this.game = game;

        if(moves != null){
            for (ChessMove move : moves) {
                positions.add(move.getEndPosition());
            }
        }

    }


    public void printBoard(boolean white){

        if(positions != null){
            System.out.println("TEST");
            System.out.println(positions.toString());
        }



        boolean toggle = true;
        String chessBoard = game.getBoard().toString(white);
        StringBuilder boardString = new StringBuilder();
        int chessLength = chessBoard.length();
        boardString.append(SET_BG_COLOR_LIGHT_GREY);
        boardString.append(SET_TEXT_COLOR_BLACK);
        if(white){
            boardString.append("\u2003 \u2003\u2003A\u2003 B \u2003C\u2003 D\u2003 E\u2003 F\u2003 G\u2003 H  \u2003\u2003");
        }
        else{
            boardString.append("\u2003 \u2003\u2003H\u2003 G \u2003F\u2003 E\u2003 D\u2003 C\u2003 B\u2003 A  \u2003\u2003");
        }
        boardString.append(RESET_BG_COLOR);
        boardString.append("\n");

        int row = 0;
        int col = 0;

        for(int i=0; i<chessLength; i++){

            if(white){
                row = 7 - (i / 9);
                col = i % 9;
            }
            else{
                row = 7 - (i / 9);
                col = i % 9;
            }

            System.out.println(String.valueOf(row) + String.valueOf(col));

            if(i%9 == 0){
                boardString.append(SET_BG_COLOR_LIGHT_GREY);
                if(white){
                    boardString.append("\u2003").append(8 - (i/9)).append("\u2003");
                }
                else{
                    boardString.append("\u2003").append((i / 9) + 1).append("\u2003");
                }
            }
            if(toggle){
                if(positions.contains(new ChessPosition(row + 1, col + 1))){
                    boardString.append(SET_BG_COLOR_GREEN);
                }
                else{
                    boardString.append(SET_BG_COLOR_BEIGE);
                }
            }
            else{
                if(positions.contains(new ChessPosition(row + 1, col + 1))){
                    boardString.append(SET_BG_COLOR_DARK_GREEN);
                }
                else{
                    boardString.append(SET_BG_COLOR_BROWN);
                }
            }
            toggle = !toggle;
            switch (chessBoard.charAt(i)) {
                case ' ' -> boardString.append(EMPTY);
                case 'R' -> {
                    boardString.append(SET_TEXT_COLOR_WHITE); boardString.append(BLACK_ROOK);
                }
                case 'N' -> {
                    boardString.append(SET_TEXT_COLOR_WHITE); boardString.append(BLACK_KNIGHT);
                }
                case 'B' -> {
                    boardString.append(SET_TEXT_COLOR_WHITE); boardString.append(BLACK_BISHOP);
                }
                case 'Q' -> {
                    boardString.append(SET_TEXT_COLOR_WHITE); boardString.append(BLACK_QUEEN);
                }
                case 'K' -> {
                    boardString.append(SET_TEXT_COLOR_WHITE); boardString.append(BLACK_KING);
                }
                case 'P' -> {
                    boardString.append(SET_TEXT_COLOR_WHITE); boardString.append(BLACK_PAWN);
                }
                case 'r' -> {
                    boardString.append(SET_TEXT_COLOR_BLACK); boardString.append(BLACK_ROOK);
                }
                case 'n' -> {
                    boardString.append(SET_TEXT_COLOR_BLACK); boardString.append(BLACK_KNIGHT);
                }
                case 'b' -> {
                    boardString.append(SET_TEXT_COLOR_BLACK); boardString.append(BLACK_BISHOP);
                }
                case 'q' -> {
                    boardString.append(SET_TEXT_COLOR_BLACK); boardString.append(BLACK_QUEEN);
                }
                case 'k' -> {
                    boardString.append(SET_TEXT_COLOR_BLACK); boardString.append(BLACK_KING);
                }
                case 'p' -> {
                    boardString.append(SET_TEXT_COLOR_BLACK); boardString.append(BLACK_PAWN);
                }
                case '\n' -> {
                    boardString.append(SET_TEXT_COLOR_BLACK);
                    boardString.append(SET_BG_COLOR_LIGHT_GREY);
                    if(white){
                        boardString.append("\u2003").append(8 - (i/9)).append("\u2003");
                    }
                    else{
                        boardString.append("\u2003").append((i / 9) + 1).append("\u2003");
                    }
                    boardString.append(RESET_BG_COLOR);
                    boardString.append("\n");

                }
            }

        }
        boardString.append(SET_BG_COLOR_LIGHT_GREY);
        boardString.append(SET_TEXT_COLOR_BLACK);
        if(white){
            boardString.append("\u2003 \u2003\u2003A\u2003 B \u2003C\u2003 D\u2003 E\u2003 F\u2003 G\u2003 H  \u2003\u2003");
        }
        else{
            boardString.append("\u2003 \u2003\u2003H\u2003 G \u2003F\u2003 E\u2003 D\u2003 C\u2003 B\u2003 A  \u2003\u2003");
        }
        boardString.append(RESET_BG_COLOR);
        boardString.append(RESET_TEXT_COLOR);
        boardString.append("\n");
        System.out.println(boardString);
    }


}
