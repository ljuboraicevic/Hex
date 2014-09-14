package hex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class <tt>Table</tt> represents the table on which the Hex game is played.
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class Table {

    /**
     * Matrix of bytes representing past players' past moves.
     */
    byte[][] matrix;

    /**
     * Number of empty fields left on the table.
     */
    int noOfEmptyFields;

    /**
     * Table side size.
     */
    int size;
    
    /**
     * Player who has the next move. First player = 0, second = 1.
     */
    private byte nextMovePlayer;

    /**
     * Initializes an empty Table.
     *
     * @param size Table size
     */
    public Table(int size) {
        this.matrix = new byte[size][size];
        this.size = size;
        this.noOfEmptyFields = size * size;
        this.nextMovePlayer = 0;
    }

    /**
     * 
     * Creates table from file
     * @param filename 
     */
    public Table(String filename) {
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

    public byte whosOnTheMove() {
        return nextMovePlayer;
    }
    
    /**
     * Returns an array with coordinates of empty fields on the table.
     * O(size^2)
     *
     * @return coordinates of empty fields
     */
    public Coordinate[] getEmptyFields() {
        Coordinate[] result = new Coordinate[noOfEmptyFields];

        int count = 0;
        //for each field on the table
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
    public Table deepCopy() {
        Table result = new Table(this.size);
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Coordinate c = new Coordinate(row, col);
                result.putMark(c, this.matrix[c.row][c.col]);
            }
        }
        
        return result;
    }

    public String toSingleRowString(boolean reverse) {
        StringBuilder sb = new StringBuilder();
        
        for (byte[] row : matrix) {
            for (byte field : row) {
                //sb.append(reverse ? (1 + Math.abs(field - 2)) : field);
                if (field > 0 && reverse) {
                    sb.append(1 + Math.abs(field - 2));
                } else {
                    sb.append(field);
                }
            }
        }
        
        return sb.toString();
    }
    
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
}
