package Chess_Board.Chess_Set.Pieces_Classes;

import Chess_Board.Chess_Set.Board;
import java.util.ArrayList;

public class King implements Piece {
    boolean hasMoved=false;
    private String name;
    private int[] location;
    private ArrayList<int[]> possibleMoves;
    private ArrayList<int[]> blockedMoves;

    public King(String name, int[] location){
        this.name=name;
        this.location=location;
    }

    public King(String name, int[] location, ArrayList<int[]> possibleMoves, ArrayList<int[]> blockedMoves){
        this.name=name;
        this.location=location;
        this.blockedMoves=blockedMoves;
        this.possibleMoves=possibleMoves;
    }

    /**
     * essentially overiding the compare method, I can see if possible moves contains a given move
     * had to override because moves are int[] which are objects, so the default compares instances of objects
     * @param move
     * @return
     */
    @Override
    public boolean possibleMovesContains(int[] move){
        for (int[] posMov:possibleMoves) {
            if(posMov[0]==move[0]&&posMov[1]==move[1]){return true;}
        }
        return false;
    }

    @Override
    public boolean blockedMovesContains(int[] move){
        for (int[] bloMov:blockedMoves) {
            if(bloMov[0]==move[0]&&bloMov[1]==move[1]){return true;}
        }
        return false;
    }

    /**
     * finds if a move is valid using the horizontal & vertical shift
     * @param horizontal_shift horizontal shift trying to be applied.
     * @param vertical_shift vertical shift trying to be applied.
     * @param board dictionary of the board.
     * @returns if a move is valid.
     */
    @Override
    public boolean canMove(int horizontal_shift, int vertical_shift, Board board) {
        //basic move
        if(Math.abs(horizontal_shift)<=1&&Math.abs(vertical_shift)<=1){
            return true;
        }

        //castling
        if(!(Math.abs(horizontal_shift)==2)||!(vertical_shift==0)||board.isCheck(location)){return false;}//makes sure castling is going to a valid square
        if(((Math.signum(horizontal_shift)==1&&!board.at(7,location[1]).hasMoved())||(Integer.signum(horizontal_shift)==-1&&!board.at(0,location[1]).hasMoved()))&&!hasMoved){
            for (int i = location[0]+Integer.signum(horizontal_shift); i!=location[0]+horizontal_shift+Integer.signum(horizontal_shift); i+=Integer.signum(horizontal_shift)) {
                if (!board.isEmpty(i, location[1])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canMove(int[] location, Board board){
        return canMove(location[0]-this.location[0],location[1]-this.location[1],board);
    }

    @Override
    public void updatePossibleAndBlockedMoves(Board board){
        possibleMoves = new ArrayList<int[]>();
        blockedMoves=new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (canMove(new int[]{i,j},board)){
                    if(board.canMove(location,new int[]{i,j}))possibleMoves.add(new int[]{i,j});
                    blockedMoves.add(new int[]{i,j});
                }
            }
        }
    }

    /**
     * getters and setters
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasMoved() {
        return false;
    }

    public void setHasMoved(boolean value) {
        hasMoved=value;
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
        return possibleMoves;
    }

    @Override
    public ArrayList<int[]> getBlockedMoves() {
        return blockedMoves;
    }
}
