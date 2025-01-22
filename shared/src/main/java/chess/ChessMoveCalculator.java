package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMoveCalculator {
    private ChessPiece.PieceType type;
    private ChessGame.TeamColor color;
    private ArrayList<ChessPosition> finalPositions = new ArrayList<>();
    private int[] testIndex = new int[2]; //row column
    private boolean pawn = false;

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        System.out.println("piece moves called");
        finalPositions.clear();
        type = board.getPiece(myPosition).getPieceType();
        color = board.getPiece(myPosition).getTeamColor();

        if(type == ChessPiece.PieceType.BISHOP){
            BishopMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.KING) {
            KingMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.QUEEN) {
            QueenMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.ROOK) {
            RookMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            KnightMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.PAWN) {
            PawnMoves(board, myPosition);
            pawn = true;
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
                            finalPositions.add(testPosition);
                        }
                    }
                    else {
                        finalPositions.add(testPosition);
                    }
                }
            }
        }
    }

    private void QueenMoves(ChessBoard board, ChessPosition myPosition) {
        int[] row_directions = {-1, 0, 1};
        int[] col_directions = {-1, 0, 1};

        // Checks all row directions
        for(int i = 0; i < 3; i++){
            // Checks all column directions
            for(int j = 0; j < 3; j++){
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

    private void RookMoves(ChessBoard board, ChessPosition myPosition) {
        int[] row_directions = {-1, 1, 0, 0};
        int[] col_directions = {0, 0, -1, 1};

        //test each direction
        for(int i = 0; i < 4; i++){
            //init test position to initial position
            testIndex[0] = myPosition.getRow() + row_directions[i];
            testIndex[1] = myPosition.getColumn() + col_directions[i];

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
                testIndex[1] = testIndex[1] + col_directions[i];

            }
        }
    }


    private void KnightMoves(ChessBoard board, ChessPosition myPosition) {
        int[] row_directions = {2, 2, -2, -2, 1, -1, 1, -1};
        int[] col_directions = {1, -1, 1, -1, 2, 2, -2, -2};

        //loop through every possible move
        for(int i = 0; i < 8; i++){
            //init test position to initial position
            testIndex[0] = myPosition.getRow() + row_directions[i];
            testIndex[1] = myPosition.getColumn() + col_directions[i];

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
    private void PawnMoves(ChessBoard board, ChessPosition myPosition) {
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


}
