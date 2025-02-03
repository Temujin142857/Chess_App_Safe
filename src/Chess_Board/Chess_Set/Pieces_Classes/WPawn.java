package Chess_Board.Chess_Set.Pieces_Classes;


import Chess_Board.Chess_Set.Board;
import java.util.ArrayList;

public class WPawn implements Piece {
    private boolean hasMoved=false;
    private String name;
    private int[] location;
    private ArrayList<int[]> possibleMoves;
    private ArrayList<int[]> blockedMoves;

    public WPawn(String name, int[] location){
        this.name=name;
        this.location=location;
    }

    public WPawn(String name, int[] location, ArrayList<int[]> possibleMoves, ArrayList<int[]> blockedMoves){
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
        boolean isCapturing=board.at(location[0]+horizontal_shift,location[1]+vertical_shift).getName().charAt(0)=='B'||board.isEnPassant(location,new int[]{location[0]+horizontal_shift,location[1]+vertical_shift});
        if(isCapturing&&Math.abs(horizontal_shift)==1&&vertical_shift==1){return true;}
        if(!isCapturing&&!hasMoved&&horizontal_shift==0&&vertical_shift==2&&board.isEmpty(location[0],3)&&board.isEmpty(location[0],2)){return true;}
        if(!isCapturing&&horizontal_shift==0&&vertical_shift==1&&location[1]+1<8&&board.isEmpty(location[0],location[1]+1)){return true;}
        return false;
    }

    @Override
    public boolean canMove(int[] location, Board board){
        return canMove(location[0]-this.location[0],location[1]-this.location[1],board);
    }

    //can be made more efficient, generating the i and j based on canMove rather than chacking
    @Override
    public void updatePossibleAndBlockedMoves(Board board){
        possibleMoves = new ArrayList<int[]>();
        blockedMoves=new ArrayList<>();
        if(location[1]+1<=7&&board.isEmpty(location[0],location[1]+1)) {
            possibleMoves.add(new int[]{location[0], location[1] + 1});
            if (location[1] == 1 && board.isEmpty(location[0], location[1] + 2)) {
                possibleMoves.add(new int[]{location[0], location[1] + 2});
            }
            else if(location[1]==1){blockedMoves.add(new int[]{location[0], location[1] + 2});}
        }
        else{
            blockedMoves.add(new int[]{location[0],location[1]+1});
            if(location[1]==1)blockedMoves.add(new int[]{location[0], location[1] + 2});
        }

        if(location[0]+1<=7&&location[1]+1<=7&&board.at(location[0]+1,location[1]+1).getName().charAt(0)=='B'){
            possibleMoves.add(new int[]{location[0]+1,location[1]+1});
        }
        else{blockedMoves.add(new int[]{location[0]+1,location[1]+1});}

        if(location[0]-1>=0&&location[1]+1<=7&&board.at(location[0]-1,location[1]+1).getName().charAt(0)=='B'){
            possibleMoves.add(new int[]{location[0]-1,location[1]+1});
        }
        else{blockedMoves.add(new int[]{location[0]-1,location[1]+1});}
    }

    private boolean isBlockedMove(int horizontal_shift, int vertical_shift, Board board) {
        if(Math.abs(horizontal_shift)==1&&vertical_shift==1){return true;}
        if(location[1]==6&&horizontal_shift==0&&vertical_shift==2&&!board.isEmpty(location[0],3)){return true;}
        if(horizontal_shift==0&&vertical_shift==1&&!board.isEmpty(location[0],location[1]+1)){return true;}
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
