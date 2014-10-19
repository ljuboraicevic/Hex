package hex.randomboards;

import hex.Board;
import hex.MCSimulationMove;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PairBoardAndRandomMoves {
    private final Board board;
    private final MCSimulationMove[] randomMoves;

    public PairBoardAndRandomMoves(Board board, MCSimulationMove[] randomMoves) {
        this.board = board;
        this.randomMoves = randomMoves;
    }

    public Board getBoard() {
        return board;
    }

    public MCSimulationMove[] getRandomMoves() {
        return randomMoves;
    }
    
    
}
