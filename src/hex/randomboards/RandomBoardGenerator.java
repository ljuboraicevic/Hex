package hex.randomboards;

import hex.Board;
import hex.Coordinate;
import hex.MCSimulationMove;
import hex.MonteCarloSimulation;
import java.util.LinkedList;

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
    public static LinkedList<MCSimulationMove[]> evaluateRandomBoards(
            Board board,
            int MonteCarloRepetitions,
            int trialRepetitions,
            int fromMovesToPlay,
            int toMovesToPlay) {
        
        LinkedList<MCSimulationMove[]> result = new LinkedList<>();
        
        for (int movesToPlay = fromMovesToPlay; movesToPlay <= toMovesToPlay; movesToPlay += 2) {
            for (int iCount = 0; iCount < trialRepetitions; iCount++) {
                result.add(generateAndEvaluateARandomBoard(board, movesToPlay, trialRepetitions));
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
            int movesToPlay, 
            int repetitions
    ) {
        Board boardCopy = b.deepCopy();
        generateRandomBoard(boardCopy, movesToPlay);
        Board boardCopy2 = boardCopy.deepCopy();
        
        System.out.println(boardCopy);
        
        MonteCarloSimulation sim = new MonteCarloSimulation(
                boardCopy2, 
                boardCopy, 
                boardCopy.getEmptyFields(), 
                0, 
                boardCopy.getEmptyFields().length, 
                repetitions, 
                boardCopy.getSize() * boardCopy.getSize() - (boardCopy.getEmptyFields().length - 1), 
                boardCopy.whosOnTheMove());
        
        sim.start();
        
        return sim.getAllMoves();
    }
    
    /**
     * "Plays" random moves on a board.
     * 
     * @param b Board
     * @param movesToPlay How many moves to play
     */
    private static void generateRandomBoard(Board b, int movesToPlay) {
        
        int who = b.whosOnTheMove();
        
        for (int iCount = 0; iCount < movesToPlay; iCount++) {
            boolean moveLegal = false;
            
            while (!moveLegal) {
                int intMove = (int) (Math.random() * (b.getSize() * b.getSize()));
                Coordinate move = b.intToCoordinate(intMove);
                
                if (b.isMoveLegal(move)) {
                    moveLegal = true;
                    b.putMark(move, (byte)(who + 1));
                    who = (who + 1) % 2;
                }
            }
        }
    }
}
