package hex;

/**
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public interface Player {
    
    /**
     * Decides on the next move on a given board.
     * 
     * @param b Board
     * @return Coordinates of next move
     */
    public Coordinate makeMove(Board b);
}
