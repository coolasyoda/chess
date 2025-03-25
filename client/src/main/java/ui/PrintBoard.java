package ui;

import chess.ChessGame;
import static ui.EscapeSequences.*;

public class PrintBoard {

    ChessGame game;

    public PrintBoard(ChessGame game){
        this.game = game;
        test();
        printBoard();
    }

    public void test(){
        String board = SET_BG_COLOR_WHITE +
                SET_TEXT_COLOR_BLACK +
                "    a  b  c  d  e  f  g  h    \n";
        System.out.println(board);

    }

    public void printBoard(){

        System.out.println(game.getBoard().toString());

    }




}
