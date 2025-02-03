package Chess_Board;

import Chess_Board.Chess_Set.Board;
import Chess_Board.Chess_Set.NotAPawnException;
import Chess_Board.Chess_Set.Pieces_Classes.Piece;
import Engine.Player_Engine;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.border.Border;


//make an empty chessboard out of panels, pieces will be buttons that sit on top of the panels
//the buttons will turn transparent if they are empty squares, and display the appropriate picture otherwise
//alternatively if I make a set of pieces on the appropriate background I can just change the panel
//on second click, data is sent to player, via it's move class
//get location in the player object can be used to turn a string of the form e4, or d5, ect, into an int array
//upon receiving confirmation that the move was made it flips the white_active boolean, and updates the form
//at some point changing the form updating method to not just scan everything would be nice
//button names can be the piece names, following the same convention in the board class
//square names can be integer coordinates, or chess notation
//you can see the battle inside me on whether to solve problems with math or programming lol
public class GUI {
    private boolean white_active;
    private boolean isPieceHeld;
    private int[] pieceHeld;//pretty sure this is a coordinate of the form {x,y} with x and y being ints between 0 and 7 inclusively
    private Player Wplayer;
    private Player Bplayer;
    private Board board;
    public JPanel[] panels=new JPanel[65];
    private final int width=90;
    private final int height=90;
    private final int horizontal_shift_right = 35;
    private final int vertical_shift_down=35;
    private boolean isCheckmate;
    private final MouseListenerForSquares mouselistenerforsquares=new MouseListenerForSquares();
    private final MouseListenerForPromotion mouselistenerforpromotion=new MouseListenerForPromotion();
    private final JFrame gameFrame = new JFrame("Tomio's Chessboard");
    private final JFrame menuFrame=new JFrame("Menu");

    private int[] promotionSquare={};
    private boolean white_cpu=false;
    private boolean black_cpu=false;
    private Player_Engine engine;
    private Player_Engine engine2;

    private boolean isCpuOpponent=false;
    private boolean playerIsWhite=true;
    private int depth;




    public void launch(){
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(300,200);
        menuFrame.setLocationRelativeTo ( null );
        menuFrame.setVisible(true);
        JButton playButton=new JButton();
        playButton.setText("Play");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    play(new Board());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        menuFrame.getContentPane().add(playButton);

        ButtonGroup colors=new ButtonGroup();
        JRadioButton white=new JRadioButton();
        white.setSize(50,50);
        white.setText("Play as white");
        colors.add(white);
        white.setSelected(true);
        white.setActionCommand("white");
        JRadioButton black=new JRadioButton();
        colors.add(black);
        black.setActionCommand("black");
        black.setSize(50,50);
        black.setText("Play as black");

        ActionListener colorButtons=new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals("white")){
                    playerIsWhite=true;
                }
                else if(e.getActionCommand().equals("black")){
                    playerIsWhite=false;
                }
            }
        };
        white.addActionListener(colorButtons);
        black.addActionListener(colorButtons);


        JCheckBox cpuOpponent=new JCheckBox();
        cpuOpponent.setText("Play against the computer");
        cpuOpponent.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    isCpuOpponent=true;
                }
                else if(e.getStateChange()==ItemEvent.DESELECTED){
                    isCpuOpponent=false;
                }
            }
        });

        ActionListener difficultyButtons=new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                depth=Integer.parseInt(e.getActionCommand());
            }
        };

        ButtonGroup difficulty=new ButtonGroup();
        JRadioButton hard=new JRadioButton();
        hard.setText("hard -still has some bugs, might crash");
        hard.setSize(50,50);
        hard.setActionCommand("3");
        hard.addActionListener(difficultyButtons);

        JRadioButton medium=new JRadioButton();
        medium.setText("medium");
        medium.setSize(50,50);
        medium.setActionCommand("2");
        medium.addActionListener(difficultyButtons);

        JRadioButton easy=new JRadioButton();
        easy.setText("easy");
        easy.setSize(50,50);
        easy.setSelected(true);
        easy.setActionCommand("1");
        easy.addActionListener(difficultyButtons);

        difficulty.add(hard);
        difficulty.add(easy);
        difficulty.add(medium);

        menuFrame.getContentPane().add(white);
        menuFrame.getContentPane().add(black);
        menuFrame.getContentPane().add(easy);
        menuFrame.getContentPane().add(medium);
        menuFrame.getContentPane().add(hard);
        menuFrame.getContentPane().add(cpuOpponent);
        menuFrame.getContentPane().setLayout(new FlowLayout());
        menuFrame.setVisible(true);
    }


    /**
     * sets all the graphics up to start the game
     * @param board
     * @throws IOException
     */
    public void play(Board board) throws IOException {
        white_active=true;
        isCheckmate=false;
        Bplayer=new Player('B',this);
        Wplayer=new Player('W',this);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setMinimumSize(new Dimension(820,820));
        gameFrame.setVisible(true);
        if(playerIsWhite||!isCpuOpponent){
            if(isCpuOpponent)black_cpu=true;
        }
        if(!playerIsWhite||!isCpuOpponent){
            if(isCpuOpponent)white_cpu=true;
        }
        this.board=board;
        initialiseSquares();
        initialisePieces(board);
        if(black_cpu&&white_cpu){
            engine=new Player_Engine('W',this,depth);
            engine2=new Player_Engine('B',this,depth);
            while(!isCheckmate){

                int[][] move=engine.getNextMove(board);
                pieceHeld=move[0];
                white_move(move[1][0],move[1][1]);

                move=engine2.getNextMove(board);
                pieceHeld=move[0];
                black_move(move[1][0],move[1][1]);
            }
            System.out.println("game over");
            return;
        }
        else if(white_cpu){
            engine=new Player_Engine('W',this,depth);
            int[][] move=engine.getNextMove(board);
            pieceHeld=move[0];
            white_move(move[1][0],move[1][1]);
        }
        else if(black_cpu){
            engine=new Player_Engine('B',this,depth);
        }


    }

    /**
     * sets up the squares, uses the panels array
     */
    private void initialiseSquares(){
        int x;
        int y;
        for (int i=0;i<65;i++) {
            x=(i/8)*width+ horizontal_shift_right;
            y=(7-(i%8))*height+vertical_shift_down;
            panels[i]=new JPanel();
            if (i%2==0&&((i/8)%2==0))panels[i].setBackground(Color.lightGray);
            else if (i%2==1&&((i/8)%2==1))panels[i].setBackground(Color.lightGray);
            else panels[i].setBackground(Color.white);
            panels[i].setBounds(x,y,width,height);
            panels[i].removeMouseListener(mouselistenerforpromotion);
            if(i!=64)panels[i].addMouseListener(mouselistenerforsquares);
            gameFrame.add(panels[i]);
        }
        panels[64].setBackground(new Color(115,86,4));
    }


    //file path equals, "src/" + pieceName + ".png"
    //need to test if the same names for labels/images overrides them
    //seems like on board the pieces are ordered
    //calculation is backwards, going to have to find the other occurences that turn backend into frontend

    private void initialisePieces(Board board) throws IOException {
        BufferedImage img;
        int i=-1;
        for (Piece[] columns:board.getPieces()) {
            for (Piece piece:columns) {
                i++;
                if(panels[i].getComponents().length!=0){panels[i].remove(0);}
                if(piece.getName().equals("EMPTY")){panels[i].updateUI();continue;}
                try {
                    img = ImageIO.read(new File("src/Chess_Board/Chess_Set/Pieces_Images/" + piece.getName() + ".png"));
                }
                catch(IOException e){
                    img = ImageIO.read(new File("Pieces_Images/" + piece.getName() + ".png"));
                }

                JLabel label = new JLabel(new ImageIcon(img));
                label.setSize(90, 90);
                panels[i].add(label);
                panels[i].updateUI();
            }
        }
    }

    /**
     * Listens for mouse clicks
     * Stores the held piece, tries to move the held piece to a new square
     * storing pieces as an x and y is a bit akward since panels is a one dimensional array
     * but I need both values for the move function in board
     * could make panels a two dimensional array, just using x*8+y works fine though
     */
    private class MouseListenerForSquares implements java.awt.event.MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(isCheckmate){return;}
            int x=(e.getComponent().getX()- horizontal_shift_right)/width;
            int y=(7-((e.getComponent().getY()-vertical_shift_down)/height));
            if (!isPieceHeld){isPieceHeld=true;pieceHeld=new int[]{x,y};highlightBorder(panels[x*8+y]);System.out.println("Piece selected");return;}
            else if (white_active){
                unHighlightBorder(panels[pieceHeld[0] * 8 + pieceHeld[1]]);
                isPieceHeld = false;
                System.out.println("Piece deselected");
                boolean moved=white_move(x,y);
                if (moved) {
                    if (board.at(new int[]{x, y}).getName().charAt(1) == 'P' && y == 7) {
                        try {
                            displayPromotionOptions(x, y);
                            System.out.println("finished");
                            return;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (black_cpu) {
                        int[][] move = engine.getNextMove(board);
                        pieceHeld = move[0];
                        black_move(move[1][0], move[1][1]);
                    }
                }
            }
            else {
                unHighlightBorder(panels[pieceHeld[0] * 8 + pieceHeld[1]]);
                isPieceHeld = false;
                System.out.println("Piece deselected");
                boolean moved=black_move(x,y);
                if (moved) {
                    if (board.at(new int[]{x, y}).getName().charAt(1) == 'P' && y == 0) {
                        try {
                            displayPromotionOptions(x, y);
                            System.out.println("finished");
                            return;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if(white_cpu) {
                        int[][] move = engine.getNextMove(board);
                        pieceHeld = move[0];
                        white_move(move[1][0], move[1][1]);
                    }
                }
            }

            System.out.println("finished");
        }

        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }


    private boolean white_move(int x, int y){
        int moveResult = Wplayer.move(board, pieceHeld,new int[]{x,y});
        if(moveResult>0){//move is legal
            isPieceHeld=false;white_active=false;
            moveAPieceToASquare(pieceHeld,new int[]{x,y});
            if (board.isCheckmate(board.findKing('B'))){
                for (int i=0;i<8;i++) {
                    for (int j = 0; j < 8; j++) {
                        System.out.println("at: "+i+","+j+"there is: "+board.at(i,j).getName());
                    }
                }
                isCheckmate=true;
                endGame("White");
            }
            else if (moveResult==2){//is castling
                moveAPieceToASquare(new int[]{(int)(1.75*(x-2)),y},new int[]{x+Integer.signum(pieceHeld[0]-x),y});
            }
            else if (moveResult==3){
                removePieceFromASquare(new int[]{x,y-1});
            }
        }
        else{
            System.out.println("illegal move, line 223, move was "+pieceHeld[0]+","+pieceHeld[1]+" to "+x+","+y);
            return false;
        }
        gameFrame.pack();
        return true;
    }

    private boolean black_move(int x, int y){
        int moveResult = Bplayer.move(board, pieceHeld,new int[]{x,y});
        System.out.println(moveResult);
        if(moveResult>0){//move is legal
            isPieceHeld=false;white_active=true;
            moveAPieceToASquare(pieceHeld,new int[]{x,y});

            if (board.isCheckmate(board.findKing('W'))){
                for (int i=0;i<8;i++) {
                    for (int j = 0; j < 8; j++) {
                        System.out.println("at: "+i+","+j+"there is: "+board.at(i,j).getName());
                    }
                }
                isCheckmate=true;
                endGame("Black");
            }
            else if (moveResult==2){//is castling
                moveAPieceToASquare(new int[]{(int)(1.75*(x-2)),y},new int[]{x+Integer.signum(pieceHeld[0]-x),y});
            }
            else if (moveResult==3){
                removePieceFromASquare(new int[]{x,y+1});
            }
        }
        else{
            System.out.println("illegal move, line 240, move was "+pieceHeld[0]+","+pieceHeld[1]+" to "+x+","+y);
            return false;
        }
        return true;
    }



    // assume to access andy panel given an x and y, formula is panel[x*8+y]
    public void displayPromotionOptions(int x, int y) throws IOException {
        //don't need to save the pieces that are removed, just remove all the promotion options and use initialise pieces after the piece is selected
        final String[] names={"Queen","Knight","Rook","Bishop"};
        String colour= String.valueOf(board.at(x,y).getName().charAt(0));
        BufferedImage img;
        int pIndex;
        promotionSquare=new int[]{x,y};
        for (int i = 0; i < 4; i++) {
            String pieceName = colour +names[i];
            try {
                img = ImageIO.read(new File("src/Chess_Board/Chess_Set/Pieces_Images/" + pieceName + ".png"));
            }
            catch(IOException e){
                img = ImageIO.read(new File("Pieces_Images/" + pieceName + ".png"));
            }
            JLabel option=new JLabel(new ImageIcon(img));
            option.setSize(width,height);
            pIndex=(x+i)*8+y;
            if(pIndex>63){pIndex=(x-(x+i)%7)*8+y;}
            System.out.println(panels[pIndex].getComponents().length+","+pIndex);
            if(panels[pIndex].getComponents().length!=0)panels[pIndex].remove(0);
            System.out.println(panels[pIndex].getComponents().length+","+pIndex);
            panels[pIndex].add(option);
            panels[pIndex].setName(pieceName);
            panels[pIndex].removeMouseListener(mouselistenerforsquares);
            panels[pIndex].addMouseListener(mouselistenerforpromotion);
            panels[pIndex].updateUI();
        }
    }

    private void repairBoard(){
        for (int i = 0; i < panels.length-1; i++) {
            if(panels[i].getMouseListeners()[0].equals(mouselistenerforpromotion)){
                panels[i].removeMouseListener(mouselistenerforpromotion);
                if(i!=64)panels[i].addMouseListener(mouselistenerforsquares);
            }
        }

    }

    /**
     * listens for mouse clicks on the promotion options
     * calls the backend to perform the promotion
     * returns the promotion squares to the normal listener and pieces
     * updates the graphics to have the new correct piece displayed
     */
    private class MouseListenerForPromotion implements java.awt.event.MouseListener{

        public MouseListenerForPromotion(){
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                System.out.println(e.getComponent().getName());
                board.promote(promotionSquare,e.getComponent().getName());
                repairBoard();
                initialisePieces(board);
                gameFrame.pack();
                if (black_cpu) {
                    int[][] move = engine.getNextMove(board);
                    pieceHeld = move[0];
                    black_move(move[1][0], move[1][1]);
                }
                else if(white_cpu){

                }
            } catch (NotAPawnException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) { }
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }

    /**
     * takes the colour which won
     * creates the end game popup
     * @param colour
     */
    private void endGame(String colour){
        final JFrame gameOver=new JFrame("Checkmate");
        gameOver.setMinimumSize(new Dimension(200,100));
        gameOver.setLocation(400,400);
        TextArea message=new TextArea(colour+" won!");
        message.setFont(new Font("Times New Roman",1,30));
        gameOver.add(message);
        gameOver.setVisible(true);
        System.out.println("lol, someone lost");
    }

    /**
     * removes anything on the target square
     * puts the piece from the old square onto the new one
     * @param originalLocation
     * @param desiredLocation
     */
    private void moveAPieceToASquare(int[] originalLocation, int[] desiredLocation){
        //if there is a piece on the square removes, important for capturing pieces
        //the piece is automatically removed from the original location by adding it to the desired panel
        removePieceFromASquare(desiredLocation);
        //moves the held piece to the now empty square
        panels[desiredLocation[0]*8+desiredLocation[1]].add(panels[originalLocation[0]*8+originalLocation[1]].getComponent(0));
        panels[desiredLocation[0]*8+desiredLocation[1]].updateUI();
        panels[originalLocation[0]*8+originalLocation[1]].updateUI();

        //frame.pack();
    }

    private void removePieceFromASquare(int[] location){
        if(panels[location[0]*8+location[1]].getComponents().length!=0){panels[location[0]*8+location[1]].remove(0);}
        panels[location[0]*8+location[1]].updateUI();
       // frame.pack();
    }

    private void highlightBorder(JPanel panel){
        Border compound;
        Border line = BorderFactory.createLineBorder(Color.yellow);
        compound = BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder());
        compound = BorderFactory.createCompoundBorder(compound,line);
        panel.setBorder(compound);
    }

    private void unHighlightBorder(JPanel panel){
        panel.setBorder(BorderFactory.createEmptyBorder());
    }

}

