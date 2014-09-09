package hex;

import java.util.Random;

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
     * Initializes a new PlayerMonteCarlo.
     * 
     * @param repetitions Monte Carlo simulation repetitions
     */
    public PlayerMonteCarlo(int repetitions) {
        this.repetitions = repetitions;
    }
    
    @Override
    public Coordinate makeMove(Table t) {
        //make a deep copy of the table
        Table tableCopy = t.deepCopy();
        
        //get coordinates of empty fields in the table
        Coordinate[] emptyFields = t.getEmptyFields();
        
        int bestResult = -1;
        Coordinate bestField  = null;
        int noOfEmptyFields = t.noOfEmptyFields;
        int movesPlayed = t.size * t.size - noOfEmptyFields;
        byte player = t.whosOnTheMove();
        
        //for each of the empty fields in the table
        for (int field = 0; field < emptyFields.length; field++) {
        
            int thisFieldWinSum = 0;
            
            //mark current "empty" field as this player's and then run the
            //simulation on the rest of the empty fields
            tableCopy.matrix[emptyFields[field].row][emptyFields[field].col] = player;
            
            //make repetitions
            for (int repetition = 0; repetition < repetitions; repetition++) {
                
                //get random sequence
                byte[] sequence = getRandomSequence(movesPlayed, t.size);
                
                //overlay the random sequence on top of the tableCopy
                for (int iCount = 0; iCount < sequence.length; iCount++) {
                    Coordinate c = emptyFields[iCount];
                    if (iCount != field && !t.isFieldMarked(c)) {
                        tableCopy.matrix[c.row][c.col] = sequence[iCount];
                    }
                }
                
                //check if this player won
                if (didIWin(tableCopy, player)) {
                    thisFieldWinSum++;
                }
            }
        
            //if this field is the best so far
            if (thisFieldWinSum > bestResult) {
                bestField = emptyFields[field];
            }
        }
        
        return bestField;
    }

    /**
     * Makes a random sequence of moves.
     * 
     * @param movesPlayed
     * @param tableSize
     * @return 
     */
    private byte[] getRandomSequence(int movesPlayed, int tableSize) {
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
    private byte[] getSequence(int movesPlayed, int tableSize) {
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
    private int getNumberOfFirstPlayersMoves(int movesPlayed, int tableSize) {
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
    private boolean didIWin(Table t, byte player) {
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
    private boolean anyVerticalPlayerOnPotentialFieldInRow(Table t, int row, boolean[] activeFields) {
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
    private boolean[] getPotentialsInRow(Table t, int row, boolean[] previousRowActiveFields) {
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
    private void checkForMissedActiveFields(Table t, int currentRow, boolean[] activeVerticalFieldsInRow) {
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
