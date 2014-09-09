package hex;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerMonteCarlo implements Player {

    @Override
    public Coordinate makeMove(Table t) {
        throw new UnsupportedOperationException("Not supported yet.");
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

//    public static void main(String[] args) {
//        for (int i = 1; i < 10; i++) {
//            String filename = "input"+i+".txt";
//            Table t = new Table(filename);
//            PlayerMonteCarlo pmc = new PlayerMonteCarlo();
//            System.out.println(t);
//            System.out.println("Player1 won: "+pmc.didIWin(t, (byte) 0));
//            System.out.println("Player2 won: "+pmc.didIWin(t, (byte) 1));
//        }
//    }
}
