package hex;

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
    public PlayerNeuralNetwork(String f) {
        nn = NeuralNetwork.load(f);
    }
    
    @Override
    public Coordinate makeMove(Board b) {
        //make a deep copy of the board
        Board boardCopy = b.deepCopy();
        
        //get coordinates of empty fields in the board
        Coordinate[] emptyFields = b.getEmptyFields();

        double bestResult = -1;
        Coordinate bestField  = null;
        int noOfEmptyFields = b.noOfEmptyFields;
        byte player = b.whosOnTheMove();
        
        //for each of the empty fields
        for (int field = 0; field < noOfEmptyFields; field++) {
            //set previously checked field to zero
            if (field > 0) { 
                Coordinate prev = emptyFields[field - 1];
                boardCopy.matrix[prev.row][prev.col] = (byte)0; 
            }
            
            //put mark on the field
            boardCopy.putMark(emptyFields[field], (byte)(player + 1));
            
            //ask the neural network if it likes the board
            double[] input = transformBoardToNNInput(boardCopy, player);
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
     * Transforms the board to neural network input.
     * 
     * @param b Board
     * @param player Which player is neural network, one or two
     * @return Neural network input
     */
    private static double[] transformBoardToNNInput(Board b, byte player) {
        double[] result = new double[b.size * b.size];
        
        //copy matrix to result and apply transformations
        for (int row = 0; row < b.size; row++) {
            for (int col = 0; col < b.size; col++) {
                result[row * b.size + col] = 
                        (double)(b.matrix[row][col] + f(player, b.matrix[row][col]));
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
