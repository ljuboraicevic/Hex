package hex;

import java.io.File;
import org.neuroph.core.NeuralNetwork;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerNeuralNetwork implements Player{

    /**
     * A neural network that decides if the move is good or bad.
     */
    private final NeuralNetwork nn;
    
    /**
     * Initializes a PlayerNeuralNetwork with neural network loaded from file f.
     * 
     * @param f Neural network file
     */
    public PlayerNeuralNetwork(File f) {
        nn = NeuralNetwork.createFromFile(f);
    }
    
    @Override
    public Coordinate makeMove(Table t) {
        //make a deep copy of the table
        Table tableCopy = t.deepCopy();
        
        //get coordinates of empty fields in the table
        Coordinate[] emptyFields = t.getEmptyFields();

        double bestResult = -1;
        Coordinate bestField  = null;
        int noOfEmptyFields = t.noOfEmptyFields;
        byte player = t.whosOnTheMove();
        
        //for each of the empty fields
        for (int field = 0; field < noOfEmptyFields; field++) {
            //set previously checked field to zero
            if (field > 0) { 
                Coordinate prev = emptyFields[field - 1];
                tableCopy.matrix[prev.row][prev.col] = (byte)0; 
            }
            
            //put mark on the field
            tableCopy.putMark(emptyFields[field], (byte)(player + 1));
            
            //ask the neural network if it likes the table
            double[] input = transformTableToNNInput(tableCopy, player);
            nn.setInput(input);
            nn.calculate();
            double[] result = nn.getOutput();
            
            //check if this is the best result so far
            if (result[0] > bestResult) {
                bestResult = result[0];
                bestField = emptyFields[field];
            }
        }
        
        return bestField;
    }
    
    /**
     * Transforms the table to neural network input.
     * 
     * @param t Table
     * @param player Which player is neural network, one or two
     * @return Neural network input
     */
    private static double[] transformTableToNNInput(Table t, byte player) {
        double[] result = new double[t.size * t.size];
        
        //copy matrix to result and apply transformations
        for (int row = 0; row < t.size; row++) {
            for (int col = 0; col < t.size; col++) {
                result[row * t.size + col] = 
                        (double)(t.matrix[row][col] + f(player, t.matrix[row][col])) * 10.0;
            }
        }        
        
        return result;
    }
    
    /**
     * Helper function which does a transformation of input.
     * 
     * @param player
     * @param field
     * @return 
     */
    private static byte f(byte player, byte field) {
        if      (player == 0 && field == 2) { return -3; }
        else if (player == 1 && field == 1) { return -2; }
        else if (player == 1 && field == 2) { return -1; }
        else                                { return  0; }
    }
    
}
