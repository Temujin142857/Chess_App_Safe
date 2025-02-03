package Chess_Board;

import Chess_Board.Chess_Set.Board;

import java.io.IOException;


public class Player {
     //sets the basics attributes of a player like color, and handles all actions made by a player
    private char colour;
    private GUI gui;

    /**
     * Constructor
     * @param colour the colour which this player will be using
     */
    public Player(char colour, GUI gui){
        this.colour=colour;
        this.gui=gui;
    }

    /**
     * conifrms the colour is correct then passes the request to the board
     * @param board
     * @param location1
     * @param location2
     * @return
     */
    public int move(Board board, int[] location1, int[] location2) {
        if (board.at(location1[0],location1[1]).getName().charAt(0)!=colour){
            System.out.println(board.at(location1).getName().charAt(0));
            System.out.println("wrong colour");
            return 0;
        }
        return board.move(location1,location2,this);
    }

    public void getPromotionPiece(int[] location,Board board){

    }

    /**
     * originally I made a console version of chess to test the backend, so this function accounts for locations of the form e4.
     * @param location
     * @return
     */
    public int[] getLocation(String location){
        char[] temp=location.toCharArray();
        int[] result=new int[2];
        for (int i=0;i< temp.length;i++) {
            if(Character.isDigit(temp[i]))result[i]=temp[i]-49;
            else{
                switch (temp[i]){
                    case 'a':result[i]=0;
                        break;
                    case 'b':result[i]=1;
                        break;
                    case 'c':result[i]=2;
                        break;
                    case 'd':result[i]=3;
                        break;
                    case 'e':result[i]=4;
                        break;
                    case 'f':result[i]=5;
                        break;
                    case 'g':result[i]=6;
                        break;
                    case 'h':result[i]=7;
                        break;
                }
            }
        }
        return result;
    }

    public char getColour() {
        return colour;
    }
    public GUI getGui(){return gui;}

}
