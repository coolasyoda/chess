package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMoveCalculator {
    private ChessPiece.PieceType type;
    private ChessGame.TeamColor color;
    private ArrayList<ChessPosition> finalPositions = new ArrayList<>();
    private int[] testIndex = new int[2]; //row column

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        finalPositions.clear();
        type = board.getPiece(myPosition).getPieceType();
        color = board.getPiece(myPosition).getTeamColor();

        if(type == ChessPiece.PieceType.BISHOP){
            BishopMoves(board, myPosition);
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
                            finalPositions.add(testPosition);
                        }

                        break;
                    }
                    finalPositions.add(testPosition);
                    testIndex[0] = testIndex[0] + row_directions[i];
                    testIndex[1] = testIndex[1] + col_directions[j];

                }

            }
        }

    }


    private boolean withinBoard(int[] testIndex){

        int row = testIndex[0];
        int col = testIndex[1];

        // Check if rows are within bounds
        if(row >= 1 && row <= 8){
            return false;
        }
        // Check if columns are within bounds
        if(col >= 1 && col <= 8){
            return false;
        }

        return true;
    }

    //Takes the initial and final positions and generates a Collection of valid moves
    private Collection<ChessMove> generateMoves(ChessPosition initialPosition){
        ArrayList<ChessMove> moves = new ArrayList<>();

        for(int i=0; i<finalPositions.size(); i++){
            moves.add(new ChessMove(initialPosition, finalPositions.get(i), null));
        }

        ChessPosition startTest = new ChessPosition(5,4);
        ChessPosition endTest = new ChessPosition(6,3);

        moves.add(new ChessMove(startTest, endTest, null));


        return moves;
    }


}