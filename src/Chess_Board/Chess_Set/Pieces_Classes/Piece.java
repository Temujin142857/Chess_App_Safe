package Chess_Board.Chess_Set.Pieces_Classes;

import Chess_Board.Chess_Set.Board;

import java.util.ArrayList;

public interface Piece {

    /**
     * Creates the piece corresponding to the name it is passed
     * @param name normal string that should contain the name of a piecce
     * @return a new piece of the choice
     */
 static Piece makePiece(String name, int[] location){
     if (name.charAt(1)=='R'){return new Rook(name, location);}
     else if (name.charAt(2)=='n'){return new Knight(name, location);}
     else if (name.charAt(1)=='B'){return new Bishop(name, location);}
     else if (name.charAt(1)=='Q'){return new Queen(name, location);}
     else if (name.charAt(1)=='P'&&name.charAt(0)=='W'){return new WPawn(name, location);}
     else if (name.charAt(1)=='P'&&name.charAt(0)=='B'){return new BPawn(name, location);}
     else return new King(name, location);
 }

 static Piece makePiece(String name, int[] location, ArrayList<int[]> possibleMoves, ArrayList<int[]> blockedMoves){
     if (name.charAt(1)=='R'){return new Rook(name, location, possibleMoves, blockedMoves);}
     else if (name.charAt(2)=='n'){return new Knight(name, location, possibleMoves, blockedMoves);}
     else if (name.charAt(1)=='B'){return new Bishop(name, location, possibleMoves, blockedMoves);}
     else if (name.charAt(1)=='Q'){return new Queen(name, location, possibleMoves, blockedMoves);}
     else if (name.charAt(1)=='P'&&name.charAt(0)=='W'){return new WPawn(name, location, possibleMoves, blockedMoves);}
     else if (name.charAt(1)=='P'&&name.charAt(0)=='B'){return new BPawn(name, location, possibleMoves, blockedMoves);}
     else return new King(name, location, possibleMoves, blockedMoves);
 }

    /**
     * finds if a move is valid using the horizontal & vertical shift
     * with the class updated to also store it's own location, can now overload this method
     * to accept just the square it's being asked to move to, and the game board
     * @param horizontal_shift horizontal shift trying to be applied.
     * @param vertical_shift vertical shift trying to be applied
     * @param board
     * @returns if a move is valid.
     */
    boolean canMove(int horizontal_shift, int vertical_shift, Board board);

    /**
     * Overloads the previous method
     * @param location int array representing the square we're trying to move the piece to
     * @param board
     * @return if a move is valid.
     */
    boolean canMove(int[] location, Board board);


    /**
     * Checks all the squares on the board, and stores the ones
     * that the piece can move to
     * @param board
     */
    void updatePossibleAndBlockedMoves(Board board);

    boolean possibleMovesContains(int[] move);

    boolean blockedMovesContains(int[] move);

    /**
     * getters and setters
     */
    String getName();
    int[] getLocation();
    void setLocation(int[] location);
    boolean hasMoved();
    void setHasMoved(boolean hasMoved);
    ArrayList<int[]> getPossibleMoves();
    ArrayList<int[]> getBlockedMoves();


}
