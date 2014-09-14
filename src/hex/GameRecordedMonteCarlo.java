package hex;

import java.util.LinkedList;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class GameRecordedMonteCarlo extends Game {

    /**
     * Array of players.
     */
    protected final PlayerMonteCarlo[] players;
    
    /**
     * Number of repetitions.
     */
    protected int repetitions;
    
    /**
     * Array of moves played. Used in statistic.
     */
    protected LinkedList<Table> moves;
    protected LinkedList<Double> probabilities;
    
    public GameRecordedMonteCarlo(
            Table t, 
            PlayerMonteCarlo first, 
            PlayerMonteCarlo second,
            int repetitions) {
        
        super(t, first, second);
        this.players = new PlayerMonteCarlo[2];
        this.players[0] = first;
        this.players[1] = second;
        this.moves = new LinkedList<>();
        this.probabilities = new LinkedList<>();
        this.repetitions = repetitions;
    }

    @Override
    public void play() {
        int winningPlayer = 0;

        //while game isn't over
        while (winningPlayer == 0) {
            //players take turns based on number of moves played so far
            MCSimulationMove move = players[movesPlayed % 2].makeMoveWithProbability(table);

            //players[0]'s mark is 1 and player[1]'s mark is 2
            table.putMark(move.getCoordinates(), (byte) (movesPlayed % 2 + 1));

            //add table to moves
            moves.add(table.deepCopy());
            //add probabilities
            probabilities.add(move.getProbability());
            
            //connect the field to its neighbors of the same color
            Coordinate[] sameColorNeighbors = 
                    findFieldsNeighborsOfSameColor(move.getCoordinates());

            for (Coordinate neighbor : sameColorNeighbors) {
                unionFind.union(getFieldIndex(move.getCoordinates()), 
                        getFieldIndex(neighbor));
            }

            //check if added nodes need to get connected
            if (isFieldOnPlayersEdge(move.getCoordinates())) {
                unionFind.union(getFieldIndex(move.getCoordinates()), 
                        getIndexOfAddedNodeInUF(move.getCoordinates()));
            }

            movesPlayed++;

            winningPlayer = whoWon();
        }
        
        System.out.println(table);
        System.out.println("Player " + winningPlayer + " wins!");
    }
    
    public LinkedList<Table> getAllMoves(){
        return this.moves;
    }
    
    public LinkedList<Double> getProbabilities() {
        return this.probabilities;
    }
    
    public String gameStats() {
        StringBuilder sb = new StringBuilder();
        
        for (int moveCount = 0; moveCount < this.moves.size(); moveCount++) {
            Table move = this.moves.get(moveCount);
            sb.append(move.toSingleRowString(moveCount % 2 != 0));
            sb.append(' ');
            //get the relative number of wins (# of wins divided by repetitions)
            double prob = this.probabilities.get(moveCount) / repetitions;
            sb.append(prob);
            sb.append(System.lineSeparator());
        }
        
        return sb.toString();
    }
}
