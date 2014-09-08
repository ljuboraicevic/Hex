package hex;

/**
 * The <tt>Game</tt> class represents a single game of Hex.
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class Game {
    /**
     * Table on which the game is played.
     */
    private final Table table;
    
    /**
     * Array of players.
     */
    private final Player[] players;
    
    /**
     * How many moves have been played so far in the game.
     */
    private int movesPlayed;
    
    /**
     * Union-find data structure that helps determine who (if anyone) won
     */
    private final QuickFindUF unionFind;

    /**
     * Initializes a new game.
     * 
     * @param t Table to be played on
     * @param first First player
     * @param second Second player
     */
    public Game(Table t, Player first, Player second) {
        this.movesPlayed = 0;
        this.players = new Player[2];
        this.players[0] = first;
        this.players[1] = second;
        this.table = t;
        this.unionFind = new QuickFindUF(t.size*t.size + 4);
    }
    
    /**
     * Checks who won.
     * @return 0 if game is still active, 1 or 2 if first or second player won,
     * respectively
     */
    public byte whoWon() {
        //if  table.noOfEmptyFields == 0;
        //TODO implement whoWon
        return 1;
    }
    
    /**
     * Starts the game. Players take turn until one of them wins.
     */
    public void play() {
        //while game isn't over
        while (whoWon() != 0) {
            //players take turns based on number of moves played so far
            Coordinate move = players[movesPlayed % 2].makeMove(table);
            //players[0]'s mark is 1 and player[1]'s mark is 2
            table.putMark(move, (byte) (movesPlayed % 2 + 1));
            movesPlayed++;
        }
    }
}
