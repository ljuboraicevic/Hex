package hex;

/**
 * Class MonteCarloSimulation represents a part of the whole simulation. Each 
 * Monte Carlo simulation works on a subset of all of the empty fields on the 
 * table.
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class MonteCarloSimulation extends Thread{

    private final Table t;
    private final Table originalTable;
    private final Coordinate[] emptyFields;
    private final int from;
    private final int to;
    private final int repetitions;
    private Integer bestResult;
    private Coordinate bestField;
    private final int movesPlayed;
    private final byte player;

    /**
     * Initializes a new Monte Carlo simulation.
     * 
     * @param t Copy of the table that this simulation is going to work with
     * @param originalT Original table
     * @param emptyFields Coordinates of all of the empty fields
     * @param from Start of the subset of empty fields
     * @param to End of the subset of empty fields
     * @param repetitions Number of repetitions
     * @param movesPlayed How many moves have been played so far
     * @param player For which player is simulation being run
     */
    public MonteCarloSimulation(
            Table t,
            Table originalT,
            Coordinate[] emptyFields, 
            int from, int to, 
            int repetitions,
            int movesPlayed,
            byte player) {
        
        this.t = t;
        this.originalTable = originalT;
        this.emptyFields = emptyFields;
        this.from = from;
        this.to = to;
        this.repetitions = repetitions;
        this.bestResult = -1;
        this.bestField = null;
        this.movesPlayed = movesPlayed;
        this.player = player;
    }

    @Override
    public void run() {
        //for each of the empty fields in the table that was assigned to 
        //this thread
        for (int field = from; field < to; field++) {
        
            int thisFieldWinSum = 0;
            
            //mark current "empty" field as this player's and then run the
            //simulation on the rest of the empty fields
            t.matrix[emptyFields[field].row][emptyFields[field].col] = (byte) (player + 1);
            
            //make repetitions
            for (int repetition = 0; repetition < repetitions; repetition++) {
                
                //get random sequence
                byte[] sequence = PlayerMonteCarlo.getRandomSequence(
                        movesPlayed, 
                        t.size * t.size);
                
                //overlay the random sequence on top of the tableCopy
                for (int iCount = 0; iCount < sequence.length; iCount++) {
                    Coordinate c = emptyFields[iCount];
                    if (iCount != field && !originalTable.isFieldMarked(c)) {
                        t.matrix[c.row][c.col] = sequence[iCount];
                    }
                }
                
                //check if this player won
                if (PlayerMonteCarlo.didIWin(t, player)) {
                    thisFieldWinSum++;
                }
            }
        
            //if this field is the best so far
            if (thisFieldWinSum > bestResult) {
                bestResult = thisFieldWinSum;
                bestField = emptyFields[field];
            }
        }
    }
    
    public Coordinate getBestField() {
        return bestField;
    }
    
    public int getBestResult() {
        return bestResult;
    }
}
