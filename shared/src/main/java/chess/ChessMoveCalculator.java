package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMoveCalculator {
    private ChessPiece.PieceType type;
    private ChessGame.TeamColor color;
    private ArrayList<ChessPosition> finalPositions = new ArrayList<>();
    private int[] testIndex = new int[2]; //row column

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        System.out.print("piece moves");

        finalPositions.clear();
        type = board.getPiece(myPosition).getPieceType();
        color = board.getPiece(myPosition).getTeamColor();

        if(type == ChessPiece.PieceType.BISHOP){
            BishopMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.KING) {
            KingMoves(board, myPosition);
        }

        return generateMoves(myPosition);
    }

    private void BishopMoves(ChessBoard board, ChessPosition myPosition) {
        int[] row_directions = {-1, 1};
        int[] col_directions = {-1, 1};

        // Checks all row directions
        for(int i = 0; i < 2; i++){
            // Checks all column directions
            for(int j = 0; j < 2; j++){
                //init test position to initial position
                testIndex[0] = myPosition.getRow() + row_directions[i];
                testIndex[1] = myPosition.getColumn() + col_directions[j];

                while(withinBoard(testIndex)){
                    // If space is occupied, check if it is the enemy. Regardless,
                    // stop the loop and go to next direction

                    ChessPosition testPosition = new ChessPosition(testIndex[0], testIndex[1]);


                    if(board.getPiece(testPosition) != null){
                        if(board.getPiece(testPosition).getTeamColor() != color){
                            System.out.print(testIndex[0]);
                            System.out.print(testIndex[1]);
                            System.out.print(" ");
                            finalPositions.add(testPosition);
                        }

                        break;
                    }

                    System.out.print(testIndex[0]);
                    System.out.print(testIndex[1]);
                    System.out.print(" ");

                    finalPositions.add(testPosition);
                    testIndex[0] = testIndex[0] + row_directions[i];
                    testIndex[1] = testIndex[1] + col_directions[j];

                }

            }
        }


    }

    private void KingMoves(ChessBoard board, ChessPosition myPosition) {
        int[] row_directions = {-1, 0, 1};
        int[] col_directions = {-1, 0, 1};

        // Checks all row directions
        for(int i = 0; i < 3; i++) {
            // Checks all column directions
            for (int j = 0; j < 3; j++) {

                testIndex[0] = myPosition.getRow() + row_directions[i];
                testIndex[1] = myPosition.getColumn() + col_directions[j];

                ChessPosition testPosition = new ChessPosition(testIndex[0], testIndex[1]);

                if(withinBoard(testIndex)){
                    if(board.getPiece(testPosition) != null){
                        if(board.getPiece(testPosition).getTeamColor() != color){
                            System.out.print(testIndex[0]);
                            System.out.print(testIndex[1]);
                            System.out.print(" ");
                            finalPositions.add(testPosition);
                        }
                    }
                    else {
                        System.out.print(testIndex[0]);
                        System.out.print(testIndex[1]);
                        System.out.print(" ");

                        finalPositions.add(testPosition);
                    }

                }


            }
        }

    }


    private boolean withinBoard(int[] testIndex){

        int row = testIndex[0];
        int col = testIndex[1];

//        System.out.print("\n TESTING WITHIN BOARD: ");
//        System.out.print(testIndex[0]);
//        System.out.print(" ");
//        System.out.print(testIndex[1]);


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

        for(int i=0; i<finalPositions.size(); i++){
            moves.add(new ChessMove(initialPosition, finalPositions.get(i), null));
        }



        return moves;
    }


}
