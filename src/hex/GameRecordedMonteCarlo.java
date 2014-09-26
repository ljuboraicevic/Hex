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
     * Array of moves played. Used in statistic.
     */
    protected LinkedList<Board> moves;
    protected LinkedList<Double> probabilities;
    /**
     * Linked list of arrays of all possible moves with respective probabilities
     */
    protected LinkedList<MCSimulationMove[]> unplayedMoves;
    /**
     * true - all probabilities of unplayed moves should be normalized false -
     * leave them unchainged
     */
    protected boolean normalizeProbabilities;

    public GameRecordedMonteCarlo(
            Board b,
            PlayerMonteCarlo first,
            PlayerMonteCarlo second,
            boolean normalizeProbabilities
    ) {

        super(b, first, second);
        this.players = new PlayerMonteCarlo[2];
        this.players[0] = first;
        this.players[1] = second;
        this.moves = new LinkedList<>();
        this.probabilities = new LinkedList<>();
        this.unplayedMoves = new LinkedList<MCSimulationMove[]>();
        this.normalizeProbabilities = normalizeProbabilities;
    }

    @Override
    public void play() {
        int winningPlayer = 0;

        //while game isn't over
        while (winningPlayer == 0) {
            //players take turns based on number of moves played so far
            MCSimulationMove[] allPossibleMoves = players[movesPlayed % 2].makeMoveWithProbability(board);

            //first move is one with heighest probability - by convention
            //players[0]'s mark is 1 and player[1]'s mark is 2
            board.putMark(allPossibleMoves[0].getCoordinates(), (byte) (movesPlayed % 2 + 1));

            //add board to moves
            moves.add(board.deepCopy());
            //add probabilities
            probabilities.add(allPossibleMoves[0].getProbability());

            unplayedMoves.add(allPossibleMoves);

            //connect the field to its neighbors of the same color
            Coordinate[] sameColorNeighbors
                    = findFieldsNeighborsOfSameColor(allPossibleMoves[0].getCoordinates());

            for (Coordinate neighbor : sameColorNeighbors) {
                unionFind.union(getFieldIndex(allPossibleMoves[0].getCoordinates()),
                        getFieldIndex(neighbor));
            }

            //check if added nodes need to get connected
            if (isFieldOnPlayersEdge(allPossibleMoves[0].getCoordinates())) {
                unionFind.union(getFieldIndex(allPossibleMoves[0].getCoordinates()),
                        getIndexOfAddedNodeInUF(allPossibleMoves[0].getCoordinates()));
            }

            movesPlayed++;

            winningPlayer = whoWon();
        }

        System.out.println(board);
        System.out.println("Player " + winningPlayer + " wins!");
    }

    public LinkedList<Board> getAllMoves() {
        return this.moves;
    }

    public LinkedList<Double> getProbabilities() {
        return this.probabilities;
    }

    public static Double normalizeProbability(double probability, double min, double max) {
        if (min != max) {
            return (probability - min) / (max - min);
        }
        return probability;
    }

    /**
     * creates regular game stats, containing only moves that are played
     *
     * @return
     * @throws Exception
     */
    public String gameStats() throws Exception {
        StringBuilder sb = new StringBuilder();

        for (int moveCount = 0; moveCount < this.moves.size(); moveCount++) {
            Board move = this.moves.get(moveCount);
            sb.append(move.toSingleRowString(moveCount % 2 != 0));
            int repetitions = moveCount % 2 == 0
                    ? this.players[0].getNumberOfRepetitions()
                    : this.players[1].getNumberOfRepetitions();

            //get the relative number of wins (# of wins divided by repetitions)
            double prob = this.probabilities.get(moveCount) / repetitions;
            if (prob > 1.0) {
                throw new Exception("Probability greater than one: " + prob);
            }
            sb.append(prob);
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    /**
     * creating additional stats, contains moves that are played and those that
     * were not played
     *
     * @return
     * @throws Exception
     */
//    private String additionalGameStats() throws Exception {
//        StringBuilder sb = new StringBuilder();
//        //create emptyboard
//        Board copyBoard = new Board(this.moves.get(0).size);
//        
//        int length = this.unplayedMoves.get(0).length;
//        MCSimulationMove[] a = this.unplayedMoves.get(0);
//        //insert all first moves probabilities on empty board
//        for(int i = 0; i < length; i++){
//            copyBoard.putMark(this.unplayedMoves.get(0)[i].getCoordinates(), (byte)1);
//            sb.append(copyBoard.toSingleRowString(false));
//            sb.append(this.unplayedMoves.get(0)[i].getProbability() / players[0].getNumberOfRepetitions());
//            sb.append(System.lineSeparator());
//            copyBoard.removeMark(this.unplayedMoves.get(0)[i].getCoordinates());
//        }
//        int size = this.moves.size();
//        //for all other unplayed moves, play on board after previous move 
//        
//        for (int i = 1; i < this.moves.size(); i++){
//            //copy board as is after previous move
//            copyBoard = this.moves.get(i-1).deepCopy();
//            int repetitions = i % 2 == 0 ? 
//                    this.players[0].getNumberOfRepetitions() : 
//                    this.players[1].getNumberOfRepetitions();
//            length = this.unplayedMoves.get(i).length;
//            for(int j = 0; j < length; j++){
//                //add unplayed move on board
//                copyBoard.putMark(this.unplayedMoves.get(i)[j].getCoordinates(), (byte)(i % 2 + 1));
//                //write stats
//                sb.append(copyBoard.toSingleRowString(i % 2 != 0));
//                sb.append(this.unplayedMoves.get(i)[j].getProbability() / repetitions);
//                sb.append(System.lineSeparator());
//                //remove unplayed move
//                copyBoard.removeMark(this.unplayedMoves.get(i)[j].getCoordinates());
//            }
//        }
//        
//        return sb.toString();
//
//    }
    /**
     * creating additional stats, contains moves that are played and those that
     * were not played
     *
     * @param firstPlayerOnly - should write only moves of first player - used
     * when neural network wants to learn only from one player
     * @param normalize - should probabilities be normalized
     * @return
     * @throws Exception
     */
    public String additionalGameStats(boolean firstPlayerOnly) throws Exception {

        StringBuilder sb = new StringBuilder();
        //create emptyboard
        Board copyBoard = new Board(this.moves.get(0).size);

        int length = this.unplayedMoves.get(0).length;
        MCSimulationMove[] a = this.unplayedMoves.get(0);
        double min = a[a.length - 1].getProbability();
        double max = a[0].getProbability();
        //insert all first moves probabilities on empty board
        for (int i = 0; i < length; i++) {
            copyBoard.putMark(this.unplayedMoves.get(0)[i].getCoordinates(), (byte) 1);
            sb.append(copyBoard.toSingleRowString(false));
            if (normalizeProbabilities && min != max) {
                sb.append(normalizeProbability(this.unplayedMoves.get(0)[i].getProbability(), min, max));
            } else {
                sb.append(this.unplayedMoves.get(0)[i].getProbability() / players[0].getNumberOfRepetitions());
            }
            sb.append(System.lineSeparator());
            copyBoard.removeMark(this.unplayedMoves.get(0)[i].getCoordinates());
        }
        int skip = firstPlayerOnly ? 1 : 0;
        //for all other unplayed moves, play on board after previous move 

        for (int i = 1 + skip; i < this.moves.size(); i += 1 + skip) {
            //copy board as is after previous move
            copyBoard = this.moves.get(i - 1).deepCopy();
            int repetitions = i % 2 == 0
                    ? this.players[0].getNumberOfRepetitions()
                    : this.players[1].getNumberOfRepetitions();
            length = this.unplayedMoves.get(i).length;
            a = this.unplayedMoves.get(i);
            min = a[a.length - 1].getProbability();
            max = a[0].getProbability();
            for (int j = 0; j < length; j++) {
                //add unplayed move on board
                copyBoard.putMark(this.unplayedMoves.get(i)[j].getCoordinates(), (byte) (i % 2 + 1));
                //write stats
                sb.append(copyBoard.toSingleRowString(i % 2 != 0));
                if (normalizeProbabilities && min != max) {
                    sb.append(normalizeProbability(this.unplayedMoves.get(i)[j].getProbability(), min, max));
                } else {
                    sb.append(this.unplayedMoves.get(i)[j].getProbability() / repetitions);
                }
                sb.append(System.lineSeparator());
                //remove unplayed move
                copyBoard.removeMark(this.unplayedMoves.get(i)[j].getCoordinates());
            }
        }
        return sb.toString();
    }

}
