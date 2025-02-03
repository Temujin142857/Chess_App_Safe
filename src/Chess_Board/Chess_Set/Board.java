package Chess_Board.Chess_Set;

import Chess_Board.Chess_Set.Pieces_Classes.EMPTY;
import Chess_Board.Chess_Set.Pieces_Classes.Piece;
import Chess_Board.Player;

import java.util.Iterator;

public class Board { //represents the game board
    final private String[][] WPIECENAMES ={new String[]{"WRook","WKnight","WBishop","WQueen","WKing","WBishop","WKnight","WRook"}, new String[]{"WPawn","WPawn","WPawn","WPawn","WPawn","WPawn","WPawn","WPawn"}}; //white pieces
    final private String[][] BPIECENAMES ={new String[]{"BRook","BKnight","BBishop","BQueen","BKing","BBishop","BKnight","BRook"}, new String[]{"BPawn","BPawn","BPawn","BPawn","BPawn","BPawn","BPawn","BPawn"}}; //black pieces
    final private String[] LETTERS ={"a","b","c","d","e","f","g","h"}; // letters for the horizontal lines.
    final private EMPTY empty=new EMPTY("EMPTY",null); // create an empty object, which represents an empty square on the board
    private Piece[][] pieces = new Piece[8][8]; // array that contains all the pieces.
    private Piece enPassantable=null;


    /**
     * Constructor
     * Makes the array that holds all the pieces
     * Chess_Set_Folder.Pieces_Folder.Board is made in the starting position
     */
    public Board(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j<2) { // for the squares on the first and second rank
                    pieces[i][j]= Piece.makePiece(WPIECENAMES[j][i],new int[]{i,j}); //set up white pieces
                }
                else if (j<6) { // for the squares on ranks 3 through 6
                    pieces[i][j]= empty; // fill the empty squares
                }
                else { // for the squares on ranks 7 and 8
                    pieces[i][j]=Piece.makePiece(BPIECENAMES[1-(j%2)][i],new int[]{i,j}); //sets up all the black pieces
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(!pieces[i][j].getName().equals("EMPTY")) {
                    pieces[i][j].updatePossibleAndBlockedMoves(this);
                }
            }
        }
    }

    public Board(Piece[][] pieces,Piece enPassantable){
        this.pieces = pieces;
        this.enPassantable=enPassantable;
    }

    /**
    * evaluates the possibility of the move and executes it if possible
     * note, checking for the correct colour is handled in the player class
     * note, player is only used for getting promotion piece
    * @param location1 starting location
    * @param location2 ending location
    * @return int 0 if the move isn't legal, 1 if the move is made, 2 is it's made and castling
    */
    public int move(int[] location1,int[] location2,Player player) {
        int horizontal_shift=location2[0]-location1[0];
        int vertical_shift=location2[1]-location1[1];
        if(vertical_shift==0&&horizontal_shift==0){
            //System.out.println("Can't move a piece to the square it's already on");
            return 0;
        }
        if(!isValidLocation(location1)||!isValidLocation(location2)){
            //System.out.println("invalid location, how did you click there?");
            return 0;
        }
        if(at(location2).getName().charAt(0)==at(location1).getName().charAt(0)){
            //System.out.println("you can't capture your own piece");
            return 0;
        }
        if(wouldBeCheck(location1,location2)){
            //System.out.println("can't move there, you would be in check");
            return 0;
        }

        if (at(location1).canMove(horizontal_shift,vertical_shift,this))
        {
            boolean enPassant = false;
            at(location1).setHasMoved(true);
            boolean castling=isCastling(location1,location2);
            at(location1).setLocation(location2);
            if(castling){
                //castling assumes the king was clicked, so the rook has to be found
                int[] rookLocation=new int[]{(int)(1.75*(location2[0]-2)),location1[1]};
                int[] newRookLocation=new int[]{location2[0]+Integer.signum(location1[0]-location2[0]),location2[1]};
                if(!isValidLocation(rookLocation)||!isValidLocation(newRookLocation)||at(rookLocation).getName().charAt(1)!='R'){return 0;}
                at(rookLocation).setHasMoved(true);
                at(rookLocation).setLocation(newRookLocation);
                pieces[location2[0]][location2[1]]=at(location1);
                pieces[newRookLocation[0]][newRookLocation[1]]=at(rookLocation);
                pieces[location1[0]][location1[1]]=empty;
                pieces[rookLocation[0]][rookLocation[1]]=empty;
            }
            else{
                enPassant=isEnPassant(location1,location2);
                if (enPassant&&enPassantable.getName().charAt(0)=='W'){pieces[location2[0]][location2[1]+1]=empty;}
                else if(enPassant){pieces[location2[0]][location2[1]-1]=empty;}
                pieces[location2[0]][location2[1]]=at(location1);
            }
            if(at(location1).getName().charAt(1)=='P'&&Math.abs(vertical_shift)==2){enPassantable=at(location1);}
            else {enPassantable=null;}
            pieces[location1[0]][location1[1]]=empty;
            for (Piece[] pieces: pieces){
                for (Piece piece:pieces) {
                    //second line is just for en passant
                    if(!piece.getName().equals("EMPTY")&&(piece.blockedMovesContains(location1)||piece.possibleMovesContains(location2)||piece.blockedMovesContains(location2)
                            ||((1 == Math.abs(location2[0]-piece.getLocation()[0]))&&piece.getName().charAt(1)=='P'&&at(location2).getName().charAt(1)=='P'))){
                        piece.updatePossibleAndBlockedMoves(this);
                    }
                }
            }
            if(location2[1]==7&&at(location2).getName().equals("WPawn")){
                    player.getPromotionPiece(location2,this);
            }

            else if(location2[1]==0&&at(location2).getName().equals("BPawn")){
                    player.getPromotionPiece(location2,this);
            }

            if (castling){return 2;}
            if (enPassant){return 3;}
            return 1;
        }
        //System.out.println("illegal move line 103 board");
        //System.out.println(pieces[location1[0]][location1[1]].getName());
        return 0;
    }

    /**
     * determines if a given move qualifies as en passant
     * @param location1
     * @param location2
     * @return
     */
    public boolean isEnPassant(int location1[], int location2[]){
        if(at(location1).getName().charAt(1)=='P') {
            if (Math.abs(location1[0] - location2[0]) == 1 && enPassantable != null && enPassantable.getName().charAt(0) == 'W' && at(location2[0], 3).getName().equals(enPassantable.getName())) {
                return true;
            }

            if (Math.abs(location1[0] - location2[0]) == 1 && enPassantable != null && enPassantable.getName().charAt(0) == 'B' && at(location2[0], 4).getName().equals(enPassantable.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * checks if a move is meant to be castling
     * @param location1
     * @param location2
     * @return
     */
    private boolean isCastling(int[] location1, int[] location2){
        if ((at(location1).getName().equals("WKing")||at(location1).getName().equals("BKing"))&&Math.abs(location1[0]-location2[0])==2){
            return true;
        }
        return false;
    }

    /**
     * makes sure the location is on the board
     * @param location
     * @return
     */
    public boolean isValidLocation(int[] location){
        for (int coord:location) {
            if(coord<0||coord>7){return false;}
        }
        return true;
    }

    //add a method named would king be in check, loops through every piece on the board, checks if they could move to the given square

    /**
     * takes the kingLocation of the king and checks if any pieces are attacking it
     * @param kingLocation the kingLocation of the king
     * @return
     */
    public boolean isCheck(int[] kingLocation){
        for (Piece[] pieces:this.pieces){
            for (Piece piece:pieces) {
                if (!piece.getName().equals("EMPTY") //doesn't check empty pieces
                    && piece.getName().charAt(0)!=at(kingLocation).getName().charAt(0) //only checks pieces the opposite colour as the king
                    && piece.canMove(kingLocation, this))
                {
                    //System.out.println("ischeck");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks all the possible moves of all the pieces of the opposing colour and determines if it's checkmate
     * if there's no check this happens instantaniously, otherwise it's one of the slower parts of the program
     * @param location
     * @return
     */
    public boolean isCheckmate(int[] location){
        Piece[][] pieces=this.pieces;
        if (!isCheck(location)) return false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j].getName().charAt(0)==at(location).getName().charAt(0)) {
                    for (int[] move : pieces[i][j].getPossibleMoves()) {
                        //System.out.println(pieces[i][j].getName()+" "+move[0]+","+move[1]);
                        //go through every possible move, and if one of them gets you out of check it's not checkmate
                        if (!wouldBeCheck(new int[]{i, j}, move)) {
                            System.out.println("To avoid checkmate move "+at(i,j).getName()+"to "+move[0]+","+move[1]);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * makes the move, checks if the king of the player who would make the move would still be in check afterwards
     * this helps with absolute pins, and just generally making sure people don't get their king captured
     * @param location1
     * @param location2
     * @return
     */
    public boolean wouldBeCheck(int[] location1, int[] location2){
        char colour=at(location1).getName().charAt(0);
        Piece[][] possible_board=new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                possible_board[i][j]=Piece.makePiece(this.at(i,j).getName(),new int[]{i,j});
            }
        }
        Board board2=new Board(possible_board,enPassantable);
        board2.moveWithoutCheck(location1,location2);
        int[] kingLocation=board2.findKing(colour);
        if(kingLocation==null){return true;}
        return board2.isCheck(kingLocation);
    }

    /**
     * identical to the move function, but doesn't check if the move would be check
     * is necessary as part of the process to determine if a given move would be illegal, as it lets me
     * make the move, then see if the king is in check
     * @param location1
     * @param location2
     * @return
     */
    public int moveWithoutCheck(int[] location1, int[] location2){
        int horizontal_shift=location2[0]-location1[0];
        int vertical_shift=location2[1]-location1[1];
        if(vertical_shift==0&&horizontal_shift==0){
            //System.out.println("Can't move a piece to the square it's already on");
            return 0;
        }
        if(!isValidLocation(location1)||!isValidLocation(location2)){
            //System.out.println("invalid location, board line 47");
            return 0;
        }
        if(at(location2).getName().charAt(0)==at(location1).getName().charAt(0)){
            //System.out.println("you can't capture your own piece");
            return 0;
        }

        if (at(location1).canMove(horizontal_shift,vertical_shift,this))
        {
            boolean enPassant = false;
            at(location1).setHasMoved(true);
            boolean castling=isCastling(location1,location2);
            at(location1).setLocation(location2);
            if(castling){
                //castling assumes the king was clicked, so the rook has to be found
                int[] rookLocation=new int[]{(int)(1.75*(location2[0]-2)),location1[1]};
                int[] newRookLocation=new int[]{location2[0]+Integer.signum(location1[0]-location2[0]),location2[1]};
                if(!isValidLocation(rookLocation)||!isValidLocation(newRookLocation)||at(rookLocation).getName().charAt(1)!='R'){return 0;}
                at(rookLocation).setHasMoved(true);
                at(rookLocation).setLocation(newRookLocation);
                pieces[location2[0]][location2[1]]=at(location1);
                pieces[newRookLocation[0]][newRookLocation[1]]=at(rookLocation);
                pieces[location1[0]][location1[1]]=empty;
                pieces[rookLocation[0]][rookLocation[1]]=empty;
            }
            else{
                enPassant=isEnPassant(location1,location2);
                if (enPassant&&enPassantable.getName().charAt(0)=='W'){pieces[location2[0]][location2[1]+1]=empty;}
                else if(enPassant){pieces[location2[0]][location2[1]-1]=empty;}
                pieces[location2[0]][location2[1]]=at(location1);
            }
            if(at(location1).getName().charAt(1)=='P'&&Math.abs(vertical_shift)==2){enPassantable=at(location1);}
            else {enPassantable=null;}
            pieces[location1[0]][location1[1]]=empty;
            if (castling){return 2;}
            if (enPassant){return 3;}
            return 1;
        }
        //System.out.println("illegal move line 103 board");
        //System.out.println(pieces[location1[0]][location1[1]].getName());
        return 0;
    }

    public boolean canMove(int[] location1,int[] location2){
        int horizontal_shift=location2[0]-location1[0];
        int vertical_shift=location2[1]-location1[1];
        if(vertical_shift==0&&horizontal_shift==0){
            //System.out.println("Can't move a piece to the square it's already on");
            return false;
        }
        if(!isValidLocation(location1)||!isValidLocation(location2)){
            //System.out.println("invalid location, board line 47");
            return false;
        }
        if(at(location2).getName().charAt(0)==at(location1).getName().charAt(0)){
            //System.out.println("you can't capture your own piece");
            return false;
        }

        if (at(location1).canMove(horizontal_shift,vertical_shift,this)&&!wouldBeCheck(location1,location2)){
            return true;
        }
        return false;
    }

    /**
     * retruns the location of the king of the given colour
     * can be a source of errors if there somehow isn't a king on the board
     * @param colour
     * @return
     */
    public int[] findKing(char colour) {
        for (Piece[] column: pieces) {
            for (Piece piece:column) {
                if (piece.getName().equals(colour+"King")){return piece.getLocation();}
            }
        }
        return null;
    }

    /**
     * performs a promotion on a given square, replacing the pawn with a given piece
     * @param location
     * @param newPieceName
     * @throws NotAPawnException
     */
    public void promote(int[] location, String newPieceName)throws NotAPawnException{
        //if(at(location).getName().charAt(2)!='P'){throw new NotAPawnException("Can't promote a piece that isn't a pawn");}
        pieces[location[0]][location[1]]=Piece.makePiece(newPieceName,location);
        pieces[location[0]][location[1]].updatePossibleAndBlockedMoves(this);
    }





    /**
     * Checks if a location on board is empty or not
     * @param location location desired to be checked
     * @return if location is empty or not
     */
    public boolean isEmpty(int[] location){
        return pieces[location[0]][location[1]].getName().equals("EMPTY");
    }

    public boolean isEmpty(int x, int y){
        return pieces[x][y].getName().equals("EMPTY");
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    private boolean arrayEquals(int[] array1,int[] array2){
        return array1[0]==array2[0]&&array1[1]==array2[1];
    }

    public Piece at(int[] location){
        return pieces[location[0]][location[1]];
    }

    public Piece at(int x, int y){
        return pieces[x][y];
    }

    public Piece getEnPassantable(){
        return enPassantable;
    }

    public class PieceIterator implements Iterator<Piece>{
        private int currentIndex1=0;
        private int currentIndex2=-1;

        @Override
        public boolean hasNext() {
            if(currentIndex1==7&&currentIndex2==7){
                return false;
            }
            else return true;
        }

        @Override
        public Piece next() {
            currentIndex2++;
            if(currentIndex2==8){
                currentIndex2=0;
                currentIndex1++;
            }
            return pieces[currentIndex1][currentIndex2];
        }
    }

    public Iterator<Piece> getIterator(){
        return new PieceIterator();
    }

    //-------------------------------------------------------------------------------------
    //potentially useful deprecated code
    //--------------------------------------------------------------------------------------


    public void put(int[] location1, int[] location2){
        pieces[location2[0]][location2[1]]=at(location1);
        pieces[location1[0]][location1[1]]=empty;
    }

    /**
     * returns the square the rook should go to
     * th two inputs are the location of two pieces
     * one of them is the king the other is the rook, but this method doesn't know which is which
     * @param location1
     * @param location2
     * @return
     */
    //redo this method, so that it returns a 2d array
    //where the array in result[0] is the square the piece at location 1 goes to
    //and the array in result[1] is the square the piece at location2 goes to
    private int[][] castlingLocation(int[] location1, int[] location2){
        int[][] result = new int[][]{new int[]{2,location1[1]},new int[]{3,location2[1]}};//initialised as though location2 is rook and queenside
        switch (location1[0]){
            case 0://location1 is the rook and it's queenside castling
                result[0][0]=3;
                result[1][0]=2;
            case 7://location1 is the rook and it's kingside castling
                result[0][0]=5;
                result[1][0]=6;
            default://so this only needs to account for location2 is the rook and it was kingside
                if(location2[0]==7) {
                    result[0][0] = 6;
                    result[1][0] = 5;
                }
        }
        return result;
    }


    /**
     * checks if a move is a pawn making a capture
     * @param location1
     * @param location2
     * @return
     */
    private boolean isPawnCapturing(int[] location1,int[] location2){
        return (at(location1).getName().charAt(1)=='P' && !at(location2).getName().equals("EMPTY"))||isEnPassant(location1,location2);
    }


}
