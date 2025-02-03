package Chess_Board.Chess_Set.Pieces_Classes;

import Chess_Board.Chess_Set.Board;

import java.util.ArrayList;

/**
 * represents an empty piece, so every propriety is basically null.
 */
public class EMPTY implements Piece {
    private String name;
    private int[] location;
    private ArrayList<int[]> possibleMoves;
    private ArrayList<int[]> blockedMoves;

    public EMPTY(String name, int[] location){
        this.name=name;
        this.location=location;
    }

    public EMPTY(String name, int[] location, ArrayList<int[]> irrelevant, ArrayList<int[]> irrelavent2){
        this.name=name;
        this.location=location;
    }


    /**
     * essentially overiding the compare method, I can see if possible moves contains a given move
     * had to override because moves are int[] which are objects, so the default compares instances of objects
     * @param move
     * @return
     */
    @Override
    public boolean possibleMovesContains(int[] move){
        return false;
    }

    @Override
    public boolean blockedMovesContains(int[] move){
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean canMove(int vertical_shift, int horizontal_shift, Board board) {
        return false;
    }

    @Override
    public boolean canMove(int[] location, Board board){
        return canMove(this.location[1]-location[1],this.location[0]-location[0],board);
    }

    @Override
    public boolean hasMoved() {
        return true;
    }

    @Override
    public void setHasMoved(boolean value) {

    }

    @Override
    public int[] getLocation(){
        return this.location;
    }

    @Override
    public void setLocation(int[] location){
        for (int i = 0; i < location.length; i++) {
            this.location[i]=location[i];
        }
    }

    @Override
    public ArrayList<int[]> getPossibleMoves() {
        return null;
    }

    @Override
    public void updatePossibleAndBlockedMoves(Board board){

    }

    @Override
    public ArrayList<int[]> getBlockedMoves() {
        return blockedMoves;
    }

}
