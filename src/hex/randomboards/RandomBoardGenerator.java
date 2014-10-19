package hex.randomboards;

import hex.Board;
import hex.Coordinate;
import hex.MCSimulationMove;
import hex.MonteCarloSimulation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used for generating random boards and evaluating them using Monte Carlo, 
 * without computer actually playing the game (evaluation of random boards).
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class RandomBoardGenerator {
    
    /**
     * Takes a board as input, adds a number of random moves and then does the
     * Monte Carlo simulations.
     * 
     * @param board
     * @param MonteCarloRepetitions
     * @param trialRepetitions
     * @param fromMovesToPlay
     * @param toMovesToPlay
     * @return 
     */
    public static LinkedList<PairBoardAndRandomMoves> evaluateRandomBoards(
            Board board,
            int MonteCarloRepetitions,
            int trialRepetitions,
            int fromMovesToPlay,
            int toMovesToPlay) {
        
        LinkedList<PairBoardAndRandomMoves> result = new LinkedList<>();
        
        for (int movesToPlay = fromMovesToPlay; movesToPlay <= toMovesToPlay; movesToPlay += 2) {
            for (int iCount = 0; iCount < trialRepetitions; iCount++) {
                //make a copy of the board
                Board boardCopy = board.deepCopy();
                generateRandomBoard(boardCopy, movesToPlay);
                MCSimulationMove[] rb = generateAndEvaluateARandomBoard(boardCopy, MonteCarloRepetitions);
                PairBoardAndRandomMoves pair = new PairBoardAndRandomMoves(boardCopy, rb);
                
                //add the evaluated board to the result
                result.add(pair);
            }
        }
        
        return result;
    }
    
    /**
     * Generates a random board and evaluates it.
     * 
     * @param b
     * @param movesToPlay
     * @param repetitions
     * @return Returns moves and success probabilities for each move
     */
    private static MCSimulationMove[] generateAndEvaluateARandomBoard(
            Board b, 
//            int movesToPlay, 
            int repetitions
    ) {
//        Board boardCopy = b.deepCopy();
//        generateRandomBoard(boardCopy, movesToPlay);
        //Board boardCopy2 = boardCopy.deepCopy();
        
        MonteCarloSimulation sim = new MonteCarloSimulation(
                b, 
                b, 
                b.getEmptyFields(), 
                0, 
                b.getEmptyFields().length, 
                repetitions, 
                b.getSize() * b.getSize() - (b.getEmptyFields().length - 1), 
                b.whosOnTheMove());
        
        sim.start();
        try {
            sim.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(RandomBoardGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        MCSimulationMove[] result = sim.getAllMoves();
        Arrays.sort(result, Comparator.reverseOrder());
        
        return result;
    }
    
    /**
     * "Plays" random moves on a board.
     * 
     * @param b Board
     * @param movesToPlay How many moves to play
     */
    private static void generateRandomBoard(Board b, int movesToPlay) {
        
        for (int iCount = 0; iCount < movesToPlay; iCount++) {
            boolean moveLegal = false;
            
            while (!moveLegal) {
                int intMove = (int) (Math.random() * (b.getSize() * b.getSize()));
                Coordinate move = b.intToCoordinate(intMove);
                
                if (b.isMoveLegal(move)) {
                    moveLegal = true;
                    b.putMark(move, (byte) (b.whosOnTheMove() + 1));
                }
            }
        }
    }
}
