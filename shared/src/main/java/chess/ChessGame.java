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

    public ChessGame() {
        board = new ChessBoard();
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
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return new ChessMoveCalculator().pieceMoves(getBoard(), startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        board.printBoard();
        move.printMove();

        //No piece moved exception
        if(board.getPiece(move.getStartPosition()) == null){
            throw new InvalidMoveException("NO PIECE MOVED");
        }

        //Wrong Turn Exception
        if(teamTurn != board.getPiece(move.getStartPosition()).getTeamColor()){
            throw new InvalidMoveException("WRONG TURN");
        }

        ArrayList<ChessMove> validMoves = (ArrayList<ChessMove>) board.getPiece(move.getStartPosition()).pieceMoves(board, move.getStartPosition());

        // Check every valid move. If "move" is not there, it is invalid.
        for(int i=0; i<validMoves.size(); i++){

            validMoves.get(i).printMove();
            if(validMoves.get(i).equals(move)){
                ChessPiece piece;

                if(move.getPromotionPiece() != null){
                    piece = new ChessPiece(board.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
                }
                else{
                    piece = board.getPiece(move.getStartPosition());
                }

                board.addPiece(move.getStartPosition(), null);
                board.addPiece(move.getEndPosition(), piece);
                board.printBoard();

                if(teamTurn.equals(TeamColor.WHITE)){
                    teamTurn = TeamColor.BLACK;
                }
                else {
                    teamTurn = TeamColor.WHITE;
                }

                return;
            }
        }
        throw new InvalidMoveException("INVALID MOVE");
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

        ChessPosition testPosition = null;
        ChessPosition kingPosition = null;
        ArrayList<ChessPosition> enemyEndPositions = new ArrayList<>();



        for(int i=1; i<=8; i++){
            for(int j=1; j<=8; j++){
                testPosition = new ChessPosition(i, j);
                if(board.getPiece(testPosition) != null){
                    if(board.getPiece(testPosition).getTeamColor() != teamColor){
                        ArrayList<ChessMove> pieceMoves = (ArrayList<ChessMove>) validMoves(testPosition);

                        // Add all the end positions to the array
                        for(int k=0; k<pieceMoves.size(); k++){
                            enemyEndPositions.add(pieceMoves.get(k).getEndPosition());
                            pieceMoves.get(k).printMove();
                        }
                    }
                    else if(board.getPiece(testPosition).getPieceType().equals(ChessPiece.PieceType.KING)){
                        kingPosition = testPosition;
                    }
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
