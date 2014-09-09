package hex;

/**
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public interface Player {
    
    /**
     * Decides on the next move given the table.
     * 
     * @param t Table
     * @return Coordinates of next move
     */
    public Coordinate makeMove(Table t);
}
