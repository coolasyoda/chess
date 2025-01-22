package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {


    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {


        //Iterates over all squares except top and bottom rows
        //Rows
        for(int i=0; i<8; i++){
            //columns
            for(int j=0; j<8; j++){
                if(i==1){
                    squares[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                } else if (i==6) {
                    squares[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                } else {
                    squares[i][j] = null;
                }
            }
        }
        //White "main" row
        squares[7][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        //Black "main" row
        squares[0][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);


        printBoard();
    }

    public void printBoard(){
        //rows
        for(int i=0; i<8; i++) {
            //columns
            for (int j = 0; j < 8; j++) {
                if(squares[i][j] == null){
                    System.out.print("| ");
                } else if(squares[i][j].getTeamColor() == ChessGame.TeamColor.WHITE){
                    if (squares[i][j].getPieceType() == ChessPiece.PieceType.ROOK) {
                        System.out.print("|R");
                    } else if (squares[i][j].getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        System.out.print("|N");
                    } else if (squares[i][j].getPieceType() == ChessPiece.PieceType.BISHOP) {
                        System.out.print("|B");
                    } else if (squares[i][j].getPieceType() == ChessPiece.PieceType.QUEEN) {
                        System.out.print("|Q");
                    } else if (squares[i][j].getPieceType() == ChessPiece.PieceType.KING) {
                        System.out.print("|K");
                    } else if (squares[i][j].getPieceType() == ChessPiece.PieceType.PAWN) {
                        System.out.print("|P");
                    }
                } else {
                    if (squares[i][j].getPieceType() == ChessPiece.PieceType.ROOK) {
                        System.out.print("|r");
                    } else if (squares[i][j].getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        System.out.print("|n");
                    } else if (squares[i][j].getPieceType() == ChessPiece.PieceType.BISHOP) {
                        System.out.print("|b");
                    } else if (squares[i][j].getPieceType() == ChessPiece.PieceType.QUEEN) {
                        System.out.print("|q");
                    } else if (squares[i][j].getPieceType() == ChessPiece.PieceType.KING) {
                        System.out.print("|k");
                    } else if (squares[i][j].getPieceType() == ChessPiece.PieceType.PAWN) {
                        System.out.print("|p");
                    }
                }

            }
            System.out.print("|\n");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
