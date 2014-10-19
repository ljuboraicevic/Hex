package hex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class <tt>Board</tt> represents the board on which the Hex game is played.
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class Board {

    /**
     * Matrix of bytes representing past players' past moves.
     */
    byte[][] matrix;

    /**
     * Number of empty fields left on the board.
     */
    int noOfEmptyFields;

    /**
     * Board side size.
     */
    int size;
    
    /**
     * Player who has the next move. First player = 0, second = 1.
     */
    private byte nextMovePlayer;

    /**
     * Initializes an empty Board.
     *
     * @param size board size
     */
    public Board(int size) {
        this.matrix = new byte[size][size];
        this.size = size;
        this.noOfEmptyFields = size * size;
        this.nextMovePlayer = 0;
    }

    /**
     * Creates board from file
     *
     * @param filename 
     */
    public Board(String filename) {
        try {
            this.nextMovePlayer = 0;
            Scanner scan = new Scanner(new File(filename));
            this.size = scan.nextInt();
            this.matrix = new byte[size][size];
            this.noOfEmptyFields = 0;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = scan.nextByte();
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PlayerMonteCarlo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Puts a "mark" on a field
     *
     * @param c Coordinates of the field
     * @param mark 1 or 2
     * @return true if move is legal, false otherwise
     */
    public boolean putMark(Coordinate c, byte mark) {
        //if there the field is empty
        if (!isFieldMarked(c)) {
            matrix[c.row][c.col] = mark;  //mark it
            noOfEmptyFields--;
            nextMovePlayer = (byte) ((nextMovePlayer + 1) % 2);
            return true;
        }

        //if the field has already been marked return false
        return false;
    }

    public boolean removeMark(Coordinate c){
        if(!isFieldMarked(c)){
            return false;
        } else {
            matrix[c.row][c.col] = 0;
            return true;
        }
    }
    
    public byte whosOnTheMove() {
        return nextMovePlayer;
    }
    
    /**
     * Returns an array with coordinates of empty fields on the board.
     * O(size^2)
     *
     * @return coordinates of empty fields
     */
    public Coordinate[] getEmptyFields() {
        Coordinate[] result = new Coordinate[noOfEmptyFields];

        int count = 0;
        //for each field on the board
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                //if the field is empty, add it
                if (matrix[row][col] == 0) {
                    result[count] = new Coordinate(row, col);
                    count++;
                }
            }
        }

        return result;
    }

    /**
     * Checks if a field has already been marked.
     *
     * @param c Coordinates of the field.
     * @return true if field has been marked, false otherwise
     */
    public boolean isFieldMarked(Coordinate c) {
        return matrix[c.row][c.col] != 0;
    }
    
    /**
     * Checks if the move is legal.
     * 
     * @param c Coordinates of the field played
     * @return true if move is legal, false otherwise
     */
    public boolean isMoveLegal(Coordinate c) {
        return c.row >= 0 && c.row < size && c.col >= 0 
                && c.col < size && !isFieldMarked(c);
    }
    
    /**
     * Makes a deep copy of itself.
     * 
     * @return A deep copy of itself
     */
    public Board deepCopy() {
        Board result = new Board(this.size);
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Coordinate c = new Coordinate(row, col);
                result.putMark(c, this.matrix[c.row][c.col]);
            }
        }
        
        result.noOfEmptyFields = this.noOfEmptyFields;
        result.nextMovePlayer = this.nextMovePlayer;
        
        return result;
    }

    /**
     * Converts the board to a single row String of -1s, 0s and 1s. Used for
     * input to neural network and statistics, not human readable.
     * 
     * @param reversePlayers Should player 1 and 2 be exchanged
     * @return 
     */
    public String toSingleRowString(boolean reversePlayers) {
        StringBuilder sb = new StringBuilder();
        
        for (byte[] row : matrix) {
            for (byte field : row) {
                byte b = field;
                if (field > 0 && reversePlayers) {
                    b = (byte)(1 + Math.abs(field - 2));
                }
                if (b == 2) { b = -1; }
                sb.append(b);
                sb.append(" ");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Converts the table to a human readable String that looks like a Hex board.
     * 
     * @return Human readable representation of the Hex board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < size; row++) {

            //add empty spaces to make romboid shape
            for (int iCount = 0; iCount < row; iCount++) {
                sb.append(" ");
            }

            for (int col = 0; col < size; col++) {
                sb.append(matrix[row][col]).append(" ");
            }

            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    public boolean isFieldVertical(Coordinate c) {
        return matrix[c.row][c.col] == 1;
    }

    public int getSize() {
        return size;
    }
    
    public Coordinate intToCoordinate(int i) throws IndexOutOfBoundsException {
        if (size*size <= i) { 
            throw new IndexOutOfBoundsException(
                    "i is greater than the size of the board."); 
        }
        
        int row = i / size;
        int col = i % size;
        Coordinate result = new Coordinate(row, col);
        return result;
    }
}
