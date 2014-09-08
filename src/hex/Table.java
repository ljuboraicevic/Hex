package hex;

import java.util.ArrayList;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class Table {
    byte[][] matrix;
    int noOfEmptyFields;
    int size;

    public Table(int size) {
        this.matrix = new byte[size][size];
        this.size = size;
        this.noOfEmptyFields = size*size;
    }
    
    public boolean putMark(Coordinate c, byte mark) {
        //if there the field is empty
        if (!isFieldMarked(c)) {
            matrix[c.row][c.col] = mark;  //mark it
            noOfEmptyFields--;
            return true;
        }
        
        return false;
    }

    public ArrayList<Coordinate> getEmptyFields() {
        ArrayList<Coordinate> result = new ArrayList<>();
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (matrix[row][col] == 0) {
                    result.add(new Coordinate(row, col));
                }
            }
        }
        
        return result;
    }
    
    public boolean isFieldMarked(Coordinate c) {
        return matrix[c.row][c.col] != 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (int row = 0; row < size; row++) {
            
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
    
    
}