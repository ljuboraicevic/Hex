package hex;

import java.util.ArrayList;

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
     * Union-find data structure that helps determine who (if anyone) won. Last
     * four entries represent added fields:
     * ufSize - 4 & ufSize - 3 => player 1
     * ufSize - 2 & ufSize - 1 => player 2
     */
    private final QuickFindUF unionFind;
    private final int ufSize;

    /**
     * Initializes a new game.
     * 
     * @param t Table to be played on
     * @param first First player (vertical)
     * @param second Second player (horizontal)
     */
    public Game(Table t, Player first, Player second) {
        this.movesPlayed = 0;
        this.players = new Player[2];
        this.players[0] = first;
        this.players[1] = second;
        this.table = t;
        this.ufSize = t.size*t.size + 4;
        this.unionFind = new QuickFindUF(ufSize);
    }
    
    /**
     * Checks who won.
     * @return 0 if game is still active, 1 or 2 if first or second player won,
     * respectively
     */
    public byte whoWon() {
        if (unionFind.connected(ufSize - 4, ufSize - 3)) {
            return 1;
        } else if (unionFind.connected(ufSize - 2, ufSize - 1)) {
            return 2;
        } else {
            return 0;
        }
    }
    
    /**
     * Starts the game. Players take turns until one of them wins.
     */
    public void play() {
        //while game isn't over
        while (whoWon() != 0) {
            //players take turns based on number of moves played so far
            Coordinate move = players[movesPlayed % 2].makeMove(table);
            
            //players[0]'s mark is 1 and player[1]'s mark is 2
            table.putMark(move, (byte) (movesPlayed % 2 + 1));
            
            //check if added nodes need to get connected
            connectAddedEdgeInUFIfNecessary(move);
            
            movesPlayed++;
        }
    }
    
    /**
     * Checks if the field is on one of the edges of the table.
     * 
     * @param c Coordinates of the field
     * @return true if field is on the edge, false otherwise
     */
    private boolean isFieldOnPlayersEdge(Coordinate c) {
        int size = table.size - 1;
        int player = movesPlayed % 2;
        
        //for the first player check vertical edges
        if (player == 0) {
            return c.row == 0 || c.row == size;
        }
        //for the second player check horizontal edges
        else {
            return c.col == 0 || c.col == size;
        }
    }
    
    /**
     * Connects the field to the appropriate added node in union find, if 
     * necessary.
     * 
     * @param c Coordinates of the field
     */
    private void connectAddedEdgeInUFIfNecessary(Coordinate c) {
        int size = table.size - 1;
        int player = movesPlayed % 2;
        int moveIndex = c.row * size + c.col;
        
        if (isFieldOnPlayersEdge(c)) {
            //for the first player check vertical edges
            if (player == 0) {
                unionFind.union(moveIndex, getIndexOfAddedNodeInUF(c));
            }
            //for the second player check horizontal edges
            else {
                unionFind.union(moveIndex, getIndexOfAddedNodeInUF(c));
            }
        }
    }
    
    /**
     * Determines added node's index in union find based on the field.
     * 
     * @param c Coordinates of the field on the edge whose added edge needs to
     * be found
     * @return Index of the added edge in union find
     */
    private int getIndexOfAddedNodeInUF(Coordinate c) {
        int size = table.size - 1;
        if      (c.row == 0)    { return size - 4; } 
        else if (c.row == size) { return size - 3; } 
        else if (c.col == 0)    { return size - 2; }
        else if (c.col == size) { return size - 1; }
        
        return 0;
    }
    
    private ArrayList<Coordinate> findFieldsNeighbors() {
        ArrayList<Coordinate> result = new ArrayList<>();
        return result;
    }
}
