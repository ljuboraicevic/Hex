package hex;

import java.io.File;
import org.neuroph.core.NeuralNetwork;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerNeuralNetwork implements Player{

    NeuralNetwork nn;
    
    public PlayerNeuralNetwork(File f) {
        nn = NeuralNetwork.createFromFile(f);
    }
    
//    @Override
//    public Coordinate makeMove(Table t) {
//        //make a deep copy of the table
//        Table tableCopy = t.deepCopy();
//        
//        //get coordinates of empty fields in the table
//        Coordinate[] emptyFields = t.getEmptyFields();
//
//        int bestResult = -1;
//        Coordinate bestField  = null;
//        int noOfEmptyFields = t.noOfEmptyFields;
//        int movesPlayed = t.size * t.size - noOfEmptyFields;
//        byte player = t.whosOnTheMove();
//        
//        
//        
//        return null;
//    }

    @Override
    public Coordinate makeMove(Table t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
