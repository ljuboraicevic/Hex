package hex;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerMonteCarlo implements Player {

    /**
     * Determines how many times Monte Carlo simulation is run.
     */
    private final int repetitions;
    
    /**
     * How many threads should be used when doing the simulation.
     */
    private final int threads;

    /**
     * Initializes a new PlayerMonteCarlo.
     * 
     * @param repetitions Monte Carlo simulation repetitions
     * @param threads How many threads to use during the simulation
     */
    public PlayerMonteCarlo(int repetitions, int threads) {
        this.repetitions = repetitions;
        this.threads = threads;
    }
    
    @Override
    public Coordinate makeMove(Board t) {
        return makeMoveWithProbability(t).getCoordinates();
    }
    
    public MCSimulationMove makeMoveWithProbability(Board b) {
        //make a deep copy of the board for each thread
        Board[] boardCopies = new Board[threads];
        for (int iCount = 0; iCount < threads; iCount++) {
            boardCopies[iCount] = b.deepCopy();
        }
        
        //get coordinates of empty fields in the board
        Coordinate[] emptyFields = b.getEmptyFields();
        
        int noOfEmptyFields = b.noOfEmptyFields;
        int movesPlayed = b.size * b.size - noOfEmptyFields;
        byte player = b.whosOnTheMove();
        
        //create simulation threads
        MonteCarloSimulation[] simArray = new MonteCarloSimulation[threads];
        int fields = b.noOfEmptyFields / threads;
        int iCount;
        for (iCount = 0; iCount < threads - 1; iCount++) {
            simArray[iCount] = new MonteCarloSimulation(
                    boardCopies[iCount], 
                    b,
                    emptyFields, 
                    iCount * fields, 
                    (iCount + 1) * fields - 1, 
                    repetitions, 
                    movesPlayed, 
                    player);
        }
        
        //last simulation
        simArray[threads - 1] = new MonteCarloSimulation(
                boardCopies[threads - 1], 
                b,
                emptyFields, 
                iCount * fields, 
                noOfEmptyFields - 1, 
                repetitions, 
                movesPlayed, 
                player);
        
        //start all threads
        for (MonteCarloSimulation mcs : simArray) {
            mcs.start();
        }
        
        //join, so that everything bellow has to wait until they're done
        for (MonteCarloSimulation mcs : simArray) {
            try {
                mcs.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(PlayerMonteCarlo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Coordinate bestField = null;
        int bestResult = -1;
        
        //go through each of the threads' best results and choose the best one
        for (iCount = 0; iCount < threads; iCount++) {
            int currentBest = simArray[iCount].getBestResult();
            if (currentBest > bestResult) {
                bestResult = currentBest;
                bestField = simArray[iCount].getBestField();
            }
        }
        
        return new MCSimulationMove(bestField, (double)bestResult);
    }
    
    /**
     * Makes a random sequence of moves.
     * 
     * @param movesPlayed
     * @param boardSize
     * @return 
     */
    public static byte[] getRandomSequence(int movesPlayed, int boardSize) {
        byte[] result = getSequence(movesPlayed, boardSize);
        shuffleArray(result);
        return result;
    }

    /**
     * Makes a non-random sequence of moves.
     * 
     * @param movesPlayed
     * @param boardSize
     * @return 
     */
    private static byte[] getSequence(int movesPlayed, int boardSize) {
        byte[] result = new byte[boardSize - movesPlayed - 1];
        int ones = getNumberOfFirstPlayersMoves(movesPlayed, boardSize);
        
        //fill in the ones and twos
        for (int iCount = 0; iCount < result.length; iCount++) {
            result[iCount] = iCount < ones ? (byte) 1 : (byte) 2;
        }
        
        return result;
    }
    
    /**
     * Returns how many ones or first player's moves should be in the random
     * sequence.
     * 
     * @param movesPlayed Moved played so far in the game
     * @param boardSize Size of the board
     * @return Number of first players moves
     */
    private static int getNumberOfFirstPlayersMoves(int movesPlayed, int boardSize) {
        int length = boardSize - movesPlayed - 1;
        int result = (int) Math.floor(length / 2);
        
        if (movesPlayed % 2 == 1) {
            if (length % 2 == 1) { result += 1; }
        }
       
        return result;
    }
    
    /**
     * Shuffle the byte array
     * 
     * @param ar Array of bytes
     */
    static void shuffleArray(byte[] ar) {
        Random random = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            byte a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
    
    /**
     * Checks if player won game. Checks from top to bottom in each row if there is vertical player
     * fields (1) that are somehow connected to active vertical players fields from previous row
     * If it doesn't find any active vertical field in row, then halt => player two won
     * 
     * @param b
     * @param player == 0 => vertical player's move, player == 1 => horizontal player's move
     * @return 
     */
    public static boolean didIWin(Board b, byte player) {
        //booleans that indicate that on [i] position in currentRow there is 
        //active vertical field field that's marked by player0
        boolean[] activeVerticalFieldsInRow = new boolean[b.size];
        
        //for first row all "previous" row fields are active 
        for (int i = 0; i < b.size; i++) {
            activeVerticalFieldsInRow[i] = true;
        }
        
        int currentRow = 0;
        boolean winnerUndetermined = true;
        byte playerWon = 0;
        
        while (winnerUndetermined && currentRow < b.size) {
            activeVerticalFieldsInRow = getPotentialsInRow(b, currentRow, activeVerticalFieldsInRow);
            checkForMissedActiveFields(b, currentRow, activeVerticalFieldsInRow);
            if (anyVerticalPlayerOnPotentialFieldInRow(b, currentRow, activeVerticalFieldsInRow)) {
                currentRow++;
            } else {
                winnerUndetermined = false;
                playerWon = 1;
            }
        }
        return playerWon == player;
    }
    /**
     * checks if there is any active field in currentRow
     * @param b
     * @param row
     * @param activeFields
     * @return 
     */
    private static boolean anyVerticalPlayerOnPotentialFieldInRow(
            Board b, 
            int row, 
            boolean[] activeFields) {
        
        for (int i = 0; i < b.size; i++) {
            if (activeFields[i] && b.isFieldVertical(new Coordinate(row, i))) {
                return true;
            }
        }
        return false;

    }
    /**
     * marks all fields that are active based on connection with active fields from previous row
     * this doesn't find all active fields in row, only ones that are connected with previous row
     * ex: t = 
     * 1 2 2
     * 1 1 1
     * 2 2 1
     * t[1][1] won't be marked as active, because fields from previous row that t[1][1]is connected to
     * (t[0][1],t[0][2]) are not active
     * @param b
     * @param row
     * @param previousRowActiveFields
     * @return 
     */
    private static boolean[] getPotentialsInRow(Board b, int row, boolean[] previousRowActiveFields) {
        boolean potentials[] = new boolean[b.size];
        for (int i = 0; i < b.size - 1; i++) {
            potentials[i] = b.isFieldVertical(new Coordinate(row, i)) && (previousRowActiveFields[i] || previousRowActiveFields[i + 1]);
        }
        potentials[b.size - 1] = b.isFieldVertical(new Coordinate(row, b.size - 1))
                && previousRowActiveFields[b.size - 1];
        return potentials;
    }
    /**
     * finds active fields in row that are horizontally connected to some active field
     * It has to be started after getPotentialsInRow()
     * This function marks t[1][1] and t[1][2] from previous example as active
     * based on connection with t[1][0] that is marked as active by getPotentialsInRow()
     * @param b
     * @param currentRow
     * @param activeVerticalFieldsInRow 
     */
    private static void checkForMissedActiveFields(Board b, int currentRow, boolean[] activeVerticalFieldsInRow) {
        for (int i = 1; i < b.size; i++) {
            if (!activeVerticalFieldsInRow[i] && b.isFieldVertical(new Coordinate(currentRow, i))) {
                activeVerticalFieldsInRow[i] = activeVerticalFieldsInRow[i - 1];
            }
        }
        for (int i = b.size - 2; i > 0; i--) {
            if (!activeVerticalFieldsInRow[i] && b.isFieldVertical(new Coordinate(currentRow, i))) {
                activeVerticalFieldsInRow[i] = activeVerticalFieldsInRow[i + 1];
            }
        }
    }

}
