package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class ChessMoveCalculator {
    private ChessPiece.PieceType type;
    private ChessGame.TeamColor color;
    private ArrayList<ChessPosition> finalPositions = new ArrayList<>();
    private int[] testIndex = new int[2]; //row column
    private boolean pawn = false;

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        finalPositions.clear();
        type = board.getPiece(myPosition).getPieceType();
        color = board.getPiece(myPosition).getTeamColor();

        if(type == ChessPiece.PieceType.BISHOP){
            bishopMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.KING) {
            kingMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.QUEEN) {
            queenMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.ROOK) {
            rookMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            knightMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.PAWN) {
            pawnMoves(board, myPosition);
            pawn = true;
        }

        return generateMoves(myPosition);
    }

    private void bishopMoves(ChessBoard board, ChessPosition myPosition) {
        int[] rowDirections = {-1, -1, 1, 1};
        int[] colDirections = {-1, 1, -1, 1};

        moveUntil(board, myPosition, rowDirections, colDirections);
    }



    private void queenMoves(ChessBoard board, ChessPosition myPosition) {
        int[] rowDirections = {1, 1, 1, -1, -1, -1, 0, 0};
        int[] colDirections = {-1, 0, 1, -1, 0, 1, 1, -1};

        moveUntil(board, myPosition, rowDirections, colDirections);

    }

    private void rookMoves(ChessBoard board, ChessPosition myPosition) {
        int[] rowDirections = {-1, 1, 0, 0};
        int[] colDirections = {0, 0, -1, 1};

        moveUntil(board, myPosition, rowDirections, colDirections);
    }

    //Looping function that handles the movements of all the pieces that move until the edges of the board
    // (bishop, rook, queen)
    private void moveUntil(ChessBoard board, ChessPosition myPosition, int[] rowDirections, int[] colDirections){

        //test each direction
        for(int i = 0; i < rowDirections.length; i++){
            //init test position to initial position
            testIndex[0] = myPosition.getRow() + rowDirections[i];
            testIndex[1] = myPosition.getColumn() + colDirections[i];

            while(withinBoard(testIndex)){
                // If space is occupied, check if it is the enemy. Regardless,
                // stop the loop and go to next direction

                ChessPosition testPosition = new ChessPosition(testIndex[0], testIndex[1]);

                if(board.getPiece(testPosition) != null){
                    if(board.getPiece(testPosition).getTeamColor() != color){
                        finalPositions.add(testPosition);
                    }

                    break;
                }

                finalPositions.add(testPosition);
                testIndex[0] = testIndex[0] + rowDirections[i];
                testIndex[1] = testIndex[1] + colDirections[i];

            }
        }
    }

    private void kingMoves(ChessBoard board, ChessPosition myPosition) {
        int[] rowDirections = {-1, 0, 1};
        int[] colDirections = {-1, 0, 1};

        // Checks all row directions
        for(int i = 0; i < 3; i++) {
            // Checks all column directions
            for (int j = 0; j < 3; j++) {

                testIndex[0] = myPosition.getRow() + rowDirections[i];
                testIndex[1] = myPosition.getColumn() + colDirections[j];

                ChessPosition testPosition = new ChessPosition(testIndex[0], testIndex[1]);

                if(withinBoard(testIndex)){
                    if(board.getPiece(testPosition) != null && board.getPiece(testPosition).getTeamColor() != color){
                            finalPositions.add(testPosition);
                    }
                    else if (board.getPiece(testPosition) == null){
                        finalPositions.add(testPosition);
                    }
                }
            }
        }
    }


    private void knightMoves(ChessBoard board, ChessPosition myPosition) {
        int[] rowDirections = {2, 2, -2, -2, 1, -1, 1, -1};
        int[] colDirections = {1, -1, 1, -1, 2, 2, -2, -2};

        //loop through every possible move
        for(int i = 0; i < 8; i++){
            //init test position to initial position
            testIndex[0] = myPosition.getRow() + rowDirections[i];
            testIndex[1] = myPosition.getColumn() + colDirections[i];

            if(withinBoard(testIndex)){
                // If space is occupied, check if it is the enemy.

                ChessPosition testPosition = new ChessPosition(testIndex[0], testIndex[1]);

                if(board.getPiece(testPosition) != null){
                    if(board.getPiece(testPosition).getTeamColor() != color){
                        finalPositions.add(testPosition);
                    }
                }
                else{
                    finalPositions.add(testPosition);
                }
            }
        }
    }

    // A little messier than I would like, but the pawn is more complicated that the other pieces :)
    private void pawnMoves(ChessBoard board, ChessPosition myPosition) {
        //If pawn is White (moving bottom to top)
        if(color == ChessGame.TeamColor.WHITE){
            ChessPosition forwardPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
            //moving forwards (no captures allowed)
            if(board.getPiece(forwardPosition) == null){
                finalPositions.add(forwardPosition);
                //If we are at the beginning, we can move 2 spaces
                if(myPosition.getRow() == 2){
                    ChessPosition forwardPosition2 = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn());
                    if(board.getPiece(forwardPosition2) == null) {
                        finalPositions.add(forwardPosition2);
                    }
                }
            }

            //Diagonal Cases
            if(withinBoard(new int[]{myPosition.getRow()+1, myPosition.getColumn()+1})){
                ChessPosition diagonalPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
                if(board.getPiece(diagonalPosition) != null && board.getPiece(diagonalPosition).getTeamColor() != color){
                    finalPositions.add(diagonalPosition);
                }
            }
            if(withinBoard(new int[]{myPosition.getRow()+1, myPosition.getColumn()-1})){
                ChessPosition diagonalPosition2 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
                if(board.getPiece(diagonalPosition2) != null && board.getPiece(diagonalPosition2).getTeamColor() != color){
                    finalPositions.add(diagonalPosition2);
                }
            }
        }
        else {  //If pawn is Black (moving top to bottom)
            ChessPosition forwardPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
            //moving forwards (no captures allowed)
            if(board.getPiece(forwardPosition) == null){
                finalPositions.add(forwardPosition);
                //If we are at the beginning, we can move 2 spaces
                if(myPosition.getRow() == 7){
                    ChessPosition forwardPosition2 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn());
                    if(board.getPiece(forwardPosition2) == null) {
                        finalPositions.add(forwardPosition2);
                    }
                }
            }

            //Diagonal Cases
            if(withinBoard(new int[]{myPosition.getRow()-1, myPosition.getColumn()+1})){
                ChessPosition diagonalPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
                if(board.getPiece(diagonalPosition) != null && board.getPiece(diagonalPosition).getTeamColor() != color){
                    finalPositions.add(diagonalPosition);
                }
            }
            if(withinBoard(new int[]{myPosition.getRow()-1, myPosition.getColumn()-1})){
                ChessPosition diagonalPosition2 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
                if(board.getPiece(diagonalPosition2) != null && board.getPiece(diagonalPosition2).getTeamColor() != color){
                    finalPositions.add(diagonalPosition2);
                }
            }
        }
    }



    private boolean withinBoard(int[] testIndex){

        int row = testIndex[0];
        int col = testIndex[1];

        // Check if rows are within bounds
        if(row < 1 || row > 8){
//            System.out.print(" row out of bounds\n");
            return false;
        }
        // Check if columns are within bounds
        if(col < 1 || col > 8){
//            System.out.print(" col out of bounds\n");
            return false;
        }

//        System.out.print(" in bounds\n");
        return true;
    }

    //Takes the initial and final positions and generates a Collection of valid moves
    private Collection<ChessMove> generateMoves(ChessPosition initialPosition){
        ArrayList<ChessMove> moves = new ArrayList<>();

        if(!pawn){
            for(int i=0; i<finalPositions.size(); i++){
                moves.add(new ChessMove(initialPosition, finalPositions.get(i), null));
            }
        }
        else{
            for(int i=0; i<finalPositions.size(); i++){
                //Pawns can't be in rows 1 or 8 unless they are promoting
                if(finalPositions.get(i).getRow() == 1 || finalPositions.get(i).getRow() == 8){
                    moves.add(new ChessMove(initialPosition, finalPositions.get(i), ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(initialPosition, finalPositions.get(i), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(initialPosition, finalPositions.get(i), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(initialPosition, finalPositions.get(i), ChessPiece.PieceType.KNIGHT));
                }
                else{
                    moves.add(new ChessMove(initialPosition, finalPositions.get(i), null));
                }
            }
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMoveCalculator that = (ChessMoveCalculator) o;
        return pawn == that.pawn && type == that.type && color == that.color && Objects.equals(finalPositions,
                that.finalPositions) && Objects.deepEquals(testIndex, that.testIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color, finalPositions, Arrays.hashCode(testIndex), pawn);
    }
}
