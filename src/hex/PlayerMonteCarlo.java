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
    public Coordinate makeMove(Table t) {
        return makeMoveWithProbability(t).getCoordinates();
    }
    
    public MCSimulationMove makeMoveWithProbability(Table t) {
        //make a deep copy of the table for each thread
        Table[] tableCopies = new Table[threads];
        for (int iCount = 0; iCount < threads; iCount++) {
            tableCopies[iCount] = t.deepCopy();
        }
        
        //get coordinates of empty fields in the table
        Coordinate[] emptyFields = t.getEmptyFields();
        
        int noOfEmptyFields = t.noOfEmptyFields;
        int movesPlayed = t.size * t.size - noOfEmptyFields;
        byte player = t.whosOnTheMove();
        
        //create simulation threads
        MonteCarloSimulation[] simArray = new MonteCarloSimulation[threads];
        int fields = t.noOfEmptyFields / threads;
        int iCount;
        for (iCount = 0; iCount < threads - 1; iCount++) {
            simArray[iCount] = new MonteCarloSimulation(
                    tableCopies[iCount], 
                    t,
                    emptyFields, 
                    iCount * fields, 
                    (iCount + 1) * fields - 1, 
                    repetitions, 
                    movesPlayed, 
                    player);
        }
        
        //last simulation
        simArray[threads - 1] = new MonteCarloSimulation(
                tableCopies[threads - 1], 
                t,
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
     * @param tableSize
     * @return 
     */
    public static byte[] getRandomSequence(int movesPlayed, int tableSize) {
        byte[] result = getSequence(movesPlayed, tableSize);
        shuffleArray(result);
        return result;
    }

    /**
     * Makes a non-random sequence of moves.
     * 
     * @param movesPlayed
     * @param tableSize
     * @return 
     */
    private static byte[] getSequence(int movesPlayed, int tableSize) {
        byte[] result = new byte[tableSize - movesPlayed - 1];
        int ones = getNumberOfFirstPlayersMoves(movesPlayed, tableSize);
        
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
     * @param tableSize Size of the table
     * @return Number of first players moves
     */
    private static int getNumberOfFirstPlayersMoves(int movesPlayed, int tableSize) {
        int length = tableSize - movesPlayed - 1;
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
     * @param t
     * @param player == 0 => vertical player's move, player == 1 => horizontal player's move
     * @return 
     */
    public static boolean didIWin(Table t, byte player) {
        //booleans that indicate that on [i] position in currentRow there is active vertical field
        //field that's marked by player0
        boolean[] activeVerticalFieldsInRow = new boolean[t.size];
        //for first row all "previous" row fields are active 
        for (int i = 0; i < t.size; i++) {
            activeVerticalFieldsInRow[i] = true;
        }
        int currentRow = 0;
        boolean winnerUndetermined = true;
        byte playerWon = 0;
        
        while (winnerUndetermined && currentRow < t.size) {
            activeVerticalFieldsInRow = getPotentialsInRow(t, currentRow, activeVerticalFieldsInRow);
            checkForMissedActiveFields(t, currentRow, activeVerticalFieldsInRow);
            if (anyVerticalPlayerOnPotentialFieldInRow(t, currentRow, activeVerticalFieldsInRow)) {
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
     * @param t
     * @param row
     * @param activeFields
     * @return 
     */
    private static boolean anyVerticalPlayerOnPotentialFieldInRow(Table t, int row, boolean[] activeFields) {
        for (int i = 0; i < t.size; i++) {
            if (activeFields[i] && t.isFieldVertical(new Coordinate(row, i))) {
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
     * @param t
     * @param row
     * @param previousRowActiveFields
     * @return 
     */
    private static boolean[] getPotentialsInRow(Table t, int row, boolean[] previousRowActiveFields) {
        boolean potentials[] = new boolean[t.size];
        for (int i = 0; i < t.size - 1; i++) {
            potentials[i] = t.isFieldVertical(new Coordinate(row, i)) && (previousRowActiveFields[i] || previousRowActiveFields[i + 1]);
        }
        potentials[t.size - 1] = t.isFieldVertical(new Coordinate(row, t.size - 1))
                && previousRowActiveFields[t.size - 1];
        return potentials;
    }
    /**
     * finds active fields in row that are horizontally connected to some active field
     * It has to be started after getPotentialsInRow()
     * This function marks t[1][1] and t[1][2] from previous example as active
     * based on connection with t[1][0] that is marked as active by getPotentialsInRow()
     * @param t
     * @param currentRow
     * @param activeVerticalFieldsInRow 
     */
    private static void checkForMissedActiveFields(Table t, int currentRow, boolean[] activeVerticalFieldsInRow) {
        for (int i = 1; i < t.size; i++) {
            if (!activeVerticalFieldsInRow[i] && t.isFieldVertical(new Coordinate(currentRow, i))) {
                activeVerticalFieldsInRow[i] = activeVerticalFieldsInRow[i - 1];
            }
        }
        for (int i = t.size - 2; i > 0; i--) {
            if (!activeVerticalFieldsInRow[i] && t.isFieldVertical(new Coordinate(currentRow, i))) {
                activeVerticalFieldsInRow[i] = activeVerticalFieldsInRow[i + 1];
            }
        }
    }

}
