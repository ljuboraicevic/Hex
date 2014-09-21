package hex;

/**
 * Class MonteCarloSimulation represents a part of the whole simulation. Each 
 * Monte Carlo simulation works on a subset of all of the empty fields on the 
 * board.
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class MonteCarloSimulation extends Thread{

    /**
     * Board that will be used for simulation.
     */
    private final Board boardCopy;
    
    /**
     * The original board from the game.
     */
    private final Board originalBoard;
    
    /**
     * List of empty fields on the board.
     */
    private final Coordinate[] emptyFields;
    
    /**
     * This simulation should only take some empty fields into account. Variable
     * from represents the start of range of empty fields that this simulation
     * will work with.
     */
    private final int from;
    
    /**
     * End of range of empty fields that this simulation will work with.
     */
    private final int to;
    
    /**
     * Number of repetitions of the simulation.
     */
    private final int repetitions;
    
    /**
     * Used to store the best result.
     */
    private Integer bestResult;
    
    /**
     * Used to store the field that produced the best result.
     */
    private Coordinate bestField;
    
    /**
     * Used to store coordinates of all possible moves and results for mc simulations
     */
    private MCSimulationMove[] possibleMoves;
    
    /**
     * How many moves have been played so far.
     */
    private final int movesPlayed;
    
    /**
     * Who is the player, number 1 or 2.
     */
    private final byte player;

    /**
     * Initializes a new Monte Carlo simulation.
     * 
     * @param b Copy of the board that this simulation is going to work with
     * @param originalBoard Original board
     * @param emptyFields Coordinates of all of the empty fields
     * @param from Start of the subset of empty fields
     * @param to End of the subset of empty fields
     * @param repetitions Number of repetitions
     * @param movesPlayed How many moves have been played so far
     * @param player For which player is simulation being run
     */
    public MonteCarloSimulation(
            Board b,
            Board originalBoard,
            Coordinate[] emptyFields, 
            int from, int to, 
            int repetitions,
            int movesPlayed,
            byte player) {
        
        this.boardCopy = b;
        this.originalBoard = originalBoard;
        this.emptyFields = emptyFields;
        this.from = from;
        this.to = to;
        this.repetitions = repetitions;
        this.bestResult = -1;
        this.bestField = null;
        this.movesPlayed = movesPlayed;
        this.player = player;
        this.possibleMoves = new MCSimulationMove[to - from];
    }

    @Override
    public void run() {
        //for each of the empty fields on the board that was assigned to 
        //this thread
        for (int field = from; field < to; field++) {

            int thisFieldWinSum = 0;
            
            //mark current "empty" field as this player's and then run the
            //simulation on the rest of the empty fields
            boardCopy.matrix[emptyFields[field].row][emptyFields[field].col] = 
                    (byte) (player + 1);
            
            //make repetitions
            for (int repetition = 0; repetition < repetitions; repetition++) {
                
                //get random sequence
                byte[] sequence = PlayerMonteCarlo.getRandomSequence(
                        movesPlayed, 
                        boardCopy.size * boardCopy.size);
                
                //overlay the random sequence on top of the boardCopy
                for (int iCount = 0; iCount < sequence.length; iCount++) {
                    Coordinate c = emptyFields[iCount];
                    if (iCount != field && !originalBoard.isFieldMarked(c)) {
                        boardCopy.matrix[c.row][c.col] = sequence[iCount];
                    }
                }
                
                //check if this player won
                if (PlayerMonteCarlo.didIWin(boardCopy, player)) {
                    thisFieldWinSum++;
                }
            }
            
            this.possibleMoves[field - from] = new MCSimulationMove(emptyFields[field],
                1.0 * thisFieldWinSum);
        
            //if this field is the best so far
            if (thisFieldWinSum > bestResult) {
                bestResult = thisFieldWinSum;
                bestField = emptyFields[field];
            }
        }
    }
    
    /**
     * Get the best field that this simulation came up with.
     * 
     * @return Best field
     */
    public Coordinate getBestField() {
        return bestField;
    }
    
    /**
     * Get the best result.
     * 
     * @return Best result
     */
    public int getBestResult() {
        return bestResult;
    }
    
    public MCSimulationMove[] getAllMoves(){
        return this.possibleMoves;
    }
    
}
