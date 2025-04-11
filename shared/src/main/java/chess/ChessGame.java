package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor teamTurn = TeamColor.WHITE;
    boolean isOver = false;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    // Gets list of "valid" moves from ChessMoveCalculator(). Then it makes each move and checks if the king
    // is in check. If it is, remove from the array.
    public Collection<ChessMove> validMoves(ChessPosition startPosition){
        ArrayList<ChessMove> possibleMoves = new ChessMoveCalculator().pieceMoves(getBoard(), startPosition);
        ArrayList<ChessMove> goodMoves = new ArrayList<>();


        for(int i=0; i<possibleMoves.size(); i++){
            if(tryMove(possibleMoves.get(i))){
                goodMoves.add(possibleMoves.get(i));
            }
        }

        if(isOver){
            return null;
        }

        return goodMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        //No piece moved exception
        if(board.getPiece(move.getStartPosition()) == null){
            throw new InvalidMoveException("NO PIECE MOVED");
        }

        //Wrong Turn Exception
        if(teamTurn != board.getPiece(move.getStartPosition()).getTeamColor()){
            throw new InvalidMoveException("WRONG TURN");
        }

        ArrayList<ChessMove> validMoves = (ArrayList<ChessMove>) validMoves(move.getStartPosition());
        if(validMoves == null){
            System.out.println("GAME IS OVER");
            return;
        }

        for(int i=0; i<validMoves.size(); i++){
            if(validMoves.get(i).equals(move) && tryMove(move)){
                movePiece(move);

                if(teamTurn == TeamColor.WHITE){
                    teamTurn = TeamColor.BLACK;
                }
                else{
                    teamTurn = TeamColor.WHITE;
                }

//                System.out.println("After:");
//                System.out.println(board.toString(true));

                return;

            } else if (validMoves.get(i).equals(move) && !tryMove(move)) {
                throw new InvalidMoveException("PUTS KING IN CHECK");
            }
        }

        throw new InvalidMoveException("INVALID MOVE");
    }

    //Returns true if move puts king in check
    private boolean tryMove(ChessMove move) {
        ChessGame tempGame = new ChessGame();
        ChessBoard tempBoard = new ChessBoard();
        ChessPosition tempPosition;
        for(int i=1; i<=8; i++){
            for(int j=1; j<=8; j++){
                tempPosition = new ChessPosition(i,j);
                tempBoard.addPiece(tempPosition, board.getPiece(tempPosition));
            }
        }



        tempGame.setBoard(tempBoard);
        tempGame.movePiece(move);

        if(board.getPiece(move.getStartPosition()) != null && tempGame.isInCheck(board.getPiece(move.getStartPosition()).getTeamColor())){
            return false;
        }

        return true;

    }

    //Executes move
    private void movePiece(ChessMove move){
        ChessPiece piece;

        if(move.getPromotionPiece() != null){
            piece = new ChessPiece(teamTurn, move.getPromotionPiece());
        }
        else{
            piece = board.getPiece(move.getStartPosition());
        }

        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Loop through every position on the board.
        // Check for valid moves from the opponent and
        // check against the position of the king.

        ChessPosition testPosition;
        ChessPosition kingPosition = null;
        ArrayList<ChessPosition> enemyEndPositions = new ArrayList<>();

        for(int i=1; i<=8; i++){
            for(int j=1; j<=8; j++){

                testPosition = new ChessPosition(i, j);
                if(board.getPiece(testPosition) != null && board.getPiece(testPosition).getTeamColor() != teamColor) {

                    ArrayList<ChessMove> pieceMoves = new ChessMoveCalculator().pieceMoves(board, testPosition);

                    // Add all the end positions to the array
                    for (int k = 0; k < pieceMoves.size(); k++) {
                        enemyEndPositions.add(pieceMoves.get(k).getEndPosition());
                    }

                }
                else if (board.getPiece(testPosition) != null && board.getPiece(testPosition).getPieceType().equals(ChessPiece.PieceType.KING)) {
                    kingPosition = testPosition;

                }
            }
        }


        for(int i=0; i<enemyEndPositions.size(); i++){
            if(kingPosition.equals(enemyEndPositions.get(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }

        ChessPosition testPosition;
        for(int i=1; i<=8; i++){
            for(int j=1; j<=8; j++){
                testPosition = new ChessPosition(i,j);
                if(board.getPiece(testPosition) != null && board.getPiece(testPosition).getTeamColor().equals(teamColor)){
                    if(!(validMoves(testPosition).isEmpty())){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        ChessPosition testPosition;
        for(int i=1; i<=8; i++){
            for(int j=1; j<=8; j++){
                testPosition = new ChessPosition(i,j);
                if(board.getPiece(testPosition) != null && board.getPiece(testPosition).getTeamColor() == teamColor){
                    if(validMoves(testPosition).size() != 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setIsOver(boolean isOver){
        this.isOver = isOver;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
