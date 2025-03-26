package ui;

import chess.ChessGame;
import static ui.EscapeSequences.*;

public class PrintBoard {

    ChessGame game;

    public PrintBoard(ChessGame game){
        this.game = game;
    }


    public void printBoard(boolean white){

        boolean toggle = true;
        String chessBoard = game.getBoard().toString(white);
        StringBuilder boardString = new StringBuilder();
        int chessLength = chessBoard.length();

        boardString.append(SET_BG_COLOR_LIGHT_GREY);
        boardString.append(SET_TEXT_COLOR_BLACK);

        if(white){
            boardString.append(EMPTY + " A  B   C   D  E   F  G   H    ");
        }
        else{
            boardString.append(EMPTY + " H  G   F   E  D   C  B   A    ");
        }
        boardString.append(RESET_BG_COLOR);
        boardString.append("\n");

        for(int i=0; i<chessLength; i++){

            if(i%9 == 0){
                boardString.append(SET_BG_COLOR_LIGHT_GREY);
                if(white){
                    boardString.append(" ").append(8 - (i/9)).append(" ");
                }
                else{
                    boardString.append(" ").append((i / 9) + 1).append(" ");
                }
            }


            if(toggle){
                boardString.append(SET_BG_COLOR_WHITE);
            }
            else{
                boardString.append(SET_BG_COLOR_DARK_GREY);
            }
            toggle = !toggle;

            switch (chessBoard.charAt(i)) {
                case ' ' -> boardString.append(EMPTY);
                case 'R' -> {
                    boardString.append(SET_TEXT_COLOR_RED);
                    boardString.append(WHITE_ROOK);
                }
                case 'N' -> {
                    boardString.append(SET_TEXT_COLOR_RED);
                    boardString.append(WHITE_KNIGHT);
                }
                case 'B' -> {
                    boardString.append(SET_TEXT_COLOR_RED);
                    boardString.append(WHITE_BISHOP);
                }
                case 'Q' -> {
                    boardString.append(SET_TEXT_COLOR_RED);
                    boardString.append(WHITE_QUEEN);
                }
                case 'K' -> {
                    boardString.append(SET_TEXT_COLOR_RED);
                    boardString.append(WHITE_KING);
                }
                case 'P' -> {
                    boardString.append(SET_TEXT_COLOR_RED);
                    boardString.append(WHITE_PAWN);
                }
                case 'r' -> {
                    boardString.append(SET_TEXT_COLOR_BLUE);
                    boardString.append(BLACK_ROOK);
                }
                case 'n' -> {
                    boardString.append(SET_TEXT_COLOR_BLUE);
                    boardString.append(BLACK_KNIGHT);
                }
                case 'b' -> {
                    boardString.append(SET_TEXT_COLOR_BLUE);
                    boardString.append(BLACK_BISHOP);
                }
                case 'q' -> {
                    boardString.append(SET_TEXT_COLOR_BLUE);
                    boardString.append(BLACK_QUEEN);
                }
                case 'k' -> {
                    boardString.append(SET_TEXT_COLOR_BLUE);
                    boardString.append(BLACK_KING);
                }
                case 'p' -> {
                    boardString.append(SET_TEXT_COLOR_BLUE);
                    boardString.append(BLACK_PAWN);
                }
                case '\n' -> {
                    boardString.append(SET_TEXT_COLOR_BLACK);
                    boardString.append(SET_BG_COLOR_LIGHT_GREY);
                    if(white){
                        boardString.append(" ").append(8 - (i/9)).append(" ");
                    }
                    else{
                        boardString.append(" ").append((i / 9) + 1).append(" ");
                    }
                    boardString.append(RESET_BG_COLOR);
                    boardString.append("\n");

                }
            }

        }

        boardString.append(SET_BG_COLOR_LIGHT_GREY);
        boardString.append(SET_TEXT_COLOR_BLACK);

        if(white){
            boardString.append(EMPTY + " A  B   C   D  E   F  G   H    ");
        }
        else{
            boardString.append(EMPTY + " H  G   F   E  D   C  B   A    ");
        }
        boardString.append(RESET_BG_COLOR);
        boardString.append("\n");

        System.out.println(boardString.toString());

    }


}
