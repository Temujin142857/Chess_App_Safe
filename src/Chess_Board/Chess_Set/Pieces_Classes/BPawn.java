package Chess_Board.Chess_Set.Pieces_Classes;

import Chess_Board.Chess_Set.Board;

import java.util.ArrayList;

public class BPawn implements Piece {
    private boolean hasMoved=false;
    String name;
    private int[] location;
    private ArrayList<int[]> possibleMoves;
    private ArrayList<int[]> blockedMoves;

    public BPawn(String name, int[] location){
        this.name=name;
        this.location=location;
    }

    public BPawn(String name, int[] location, ArrayList<int[]> possibleMoves, ArrayList<int[]> blockedMoves) {
        this.name = name;
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
     * checks if the move is legal
     * @param horizontal_shift the horizontal move desired
     * @param vertical_shift the vertical move desired
     * @param board the dictionary of the board
     * @return if the move is legal
     */
    @Override
    public boolean canMove(int horizontal_shift, int vertical_shift, Board board) {
        boolean isCapturing=board.at(location[0]+horizontal_shift,location[1]+vertical_shift).getName().charAt(0)=='W'||board.isEnPassant(location,new int[]{location[0]+horizontal_shift,location[1]+vertical_shift});
        if(isCapturing&&Math.abs(horizontal_shift)==1&&vertical_shift==-1){return true;}//handles en passant as well
        if(!isCapturing&&location[1]==6&&horizontal_shift==0&&vertical_shift==-2&&board.isEmpty(location[0],5)&& board.isEmpty(location[0],4)){return true;}
        if(!isCapturing&&horizontal_shift==0&&vertical_shift==-1&&board.isEmpty(location[0],location[1]-1)){return true;}
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
        //moving forward
        if(location[1]-1<=7&&board.isEmpty(location[0],location[1]-1)) {
            possibleMoves.add(new int[]{location[0], location[1] - 1});
            if (location[1] == 6 && board.isEmpty(location[0], location[1] - 2)) {
                possibleMoves.add(new int[]{location[0], location[1] - 2});
            }
            else if(location[1]==6){blockedMoves.add(new int[]{location[0], location[1] - 2});}
        }
        else{
            blockedMoves.add(new int[]{location[0],location[1]-1});
            if(location[1]==6)blockedMoves.add(new int[]{location[0], location[1] - 2});
        }

        //capturing to the right
        if(location[1]-1<=7&&location[0]+1<=7&&board.at(location[0]+1,location[1]-1).getName().charAt(0)=='W'){
            possibleMoves.add(new int[]{location[0]+1,location[1]-1});
        }
        else{blockedMoves.add(new int[]{location[0]+1,location[1]-1});}

        //capturing to the right
        if(location[1]-1<=7&&location[0]-1>=0&&board.at(location[0]-1,location[1]-1).getName().charAt(0)=='W'){
            possibleMoves.add(new int[]{location[0]-1,location[1]-1});
        }
        else{blockedMoves.add(new int[]{location[0]-1,location[1]-1});}

        //en passant right
        if(location[1]-1<=7&&location[0]+1<=7&&board.isEnPassant(location,new int[]{location[0]+1,location[1]-1})){
            possibleMoves.add(new int[]{location[0]+1,location[1]-1});
        }
        //en passant left
        if(location[1]-1<=7&&location[0]-1>=0&&board.isEnPassant(location,new int[]{location[0]-1,location[1]-1})){
            possibleMoves.add(new int[]{location[0]-1,location[1]-1});
        }
    }

    private boolean isBlockedMove(int horizontal_shift, int vertical_shift, Board board) {
        if(Math.abs(horizontal_shift)==1&&vertical_shift==-1){return true;}
        if(location[1]==6&&horizontal_shift==0&&vertical_shift==-2&&!board.isEmpty(location[0],7)){return true;}
        if(horizontal_shift==0&&vertical_shift==-1&&!board.isEmpty(location[0],location[1]-1)){return true;}
        return false;
    }

    private boolean isBlockedMove(int[] location, Board board){
        return isBlockedMove(location[0]-this.location[0],location[1]-this.location[1],board);
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
        return true;
    }

    @Override
    public void setHasMoved(boolean hasMoved){
        this.hasMoved=hasMoved;
    }

    @Override
    public int[] getLocation(){
        return this.location;
    }

    @Override
    public void setLocation(int[] location){
        System.arraycopy(location, 0, this.location, 0, location.length);
    }

    @Override
    public ArrayList<int[]> getBlockedMoves() {
        return blockedMoves;
    }

    @Override
    public ArrayList<int[]> getPossibleMoves() {
        return possibleMoves;
    }

}
