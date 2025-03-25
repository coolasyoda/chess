package ui;

import chess.ChessGame;
import static ui.EscapeSequences.*;

public class ChessBoard {

    ChessGame game;

    public ChessBoard(ChessGame game){
        this.game = game;
        test();
    }

    public void test(){
        String board = SET_BG_COLOR_WHITE +
                SET_TEXT_COLOR_BLACK +
                "    a  b  c  d  e  f  g  h    " +
                "\n";
        System.out.println(board);

    }




}
