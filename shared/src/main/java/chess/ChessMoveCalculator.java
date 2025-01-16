package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMoveCalculator {
    private ChessPiece.PieceType type;

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        type = board.getPiece(myPosition).getPieceType();

        if(type == ChessPiece.PieceType.BISHOP){
            BishopMoves(board, myPosition);
        }

        return new ArrayList<>();
    }

    private Collection<ChessMove> BishopMoves(ChessBoard board, ChessPosition myPosition) {

        return new ArrayList<>();
    }



}
