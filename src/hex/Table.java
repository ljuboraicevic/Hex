package hex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
     * Initializes an empty Table.
     *
     * @param size Table size
     */
    public Table(int size) {
        this.matrix = new byte[size][size];
        this.size = size;
        this.noOfEmptyFields = size * size;
    }

    /**
     * 
     * Creates table from file
     * @param filename 
     */
    public Table(String filename) {
        try {
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
            return true;
        }

        //if the field has already been marked return false
        return false;
    }

    /**
     * Returns an ArrayList with coordinates of empty fields on the table.
     * O(size^2)
     *
     * @return coordinates of empty fields
     */
    public ArrayList<Coordinate> getEmptyFields() {
        ArrayList<Coordinate> result = new ArrayList<>();

        //for each field on the table
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                //if the field is empty, add it
                if (matrix[row][col] == 0) {
                    result.add(new Coordinate(row, col));
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
