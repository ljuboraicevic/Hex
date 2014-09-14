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
    protected final Table table;

    /**
     * Array of players.
     */
    protected final Player[] players;

    /**
     * How many moves have been played so far in the game.
     */
    protected int movesPlayed;
    
    /**
     * Union-find data structure that helps determine who (if anyone) won. Last
     * four entries represent added fields: ufSize - 4 & ufSize - 3 => player 1
     * ufSize - 2 & ufSize - 1 => player 2
     */
    protected final UF unionFind;
    protected final int ufSize;

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
        this.ufSize = t.size * t.size + 4;
        this.unionFind = new UF(ufSize);
    }

    /**
     * Checks who won.
     *
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
        int winningPlayer = 0;

        //while game isn't over
        while (winningPlayer == 0) {
            //players take turns based on number of moves played so far
            Coordinate move = players[movesPlayed % 2].makeMove(table);

            //players[0]'s mark is 1 and player[1]'s mark is 2
            table.putMark(move, (byte) (movesPlayed % 2 + 1));

//            //if this game is being recorded
//            if (recordGame) {
//                //add table to moves
//                moves.add(table.deepCopy());
//                //add probabilities
//                probabilities.add(move.getProbability());
//            }
            
            //connect the field to its neighbors of the same color
            Coordinate[] sameColorNeighbors = findFieldsNeighborsOfSameColor(move);

            for (Coordinate neighbor : sameColorNeighbors) {
                unionFind.union(getFieldIndex(move), getFieldIndex(neighbor));
            }

            //check if added nodes need to get connected
            if (isFieldOnPlayersEdge(move)) {
                unionFind.union(getFieldIndex(move), getIndexOfAddedNodeInUF(move));
            }

            movesPlayed++;

            winningPlayer = whoWon();
        }

        System.out.println(table);
        System.out.println("Player " + winningPlayer + " wins!");
    }

    /**
     * Checks if the field is on one of the edges of the table.
     *
     * @param c Coordinates of the field
     * @return true if field is on the edge, false otherwise
     */
    protected boolean isFieldOnPlayersEdge(Coordinate c) {
        int size = table.size - 1;
        int player = movesPlayed % 2;

        //for the first player check vertical edges
        if (player == 0) {
            return c.row == 0 || c.row == size;
        } //for the second player check horizontal edges
        else {
            return c.col == 0 || c.col == size;
        }
    }

    /**
     * Calculates index of the field in the union find.
     *
     * @param c Coordinates of the field
     * @return Index of the field in the union find
     */
    protected int getFieldIndex(Coordinate c) {
        return c.row * table.size + c.col;
    }

    /**
     * Determines added node's index in union find based on the field.
     *
     * @param c Coordinates of the field on the edge whose added edge needs to
     * be found
     * @return Index of the added edge in union find
     */
    protected int getIndexOfAddedNodeInUF(Coordinate c) {
        int player = movesPlayed % 2;

       //added node for the up side
        if      (c.row == 0              && player == 0) { return ufSize - 4; }
        //added node for the down side
        else if (c.row == table.size - 1 && player == 0) { return ufSize - 3; }
        //added node for the left side
        else if (c.col == 0              && player == 1) { return ufSize - 2; }
        //added node for the right side
        else if (c.col == table.size - 1 && player == 1) { return ufSize - 1; }

        return 0;
    }

    /**
     * Finds neighbors of the field.
     *
     * @param c Coordinates of the field
     * @return Array of coordinates of the neighbors of the field
     */
    protected Coordinate[] findFieldsNeighborsOfSameColor(Coordinate c) {
        byte color = table.matrix[c.row][c.col];

        //list of all possible neighbors, even the illegal ones
        Coordinate[] neighbors = new Coordinate[6];
        neighbors[0] = new Coordinate(c.row - 1, c.col    );
        neighbors[1] = new Coordinate(c.row - 1, c.col + 1);
        neighbors[2] = new Coordinate(c.row,     c.col - 1);
        neighbors[3] = new Coordinate(c.row,     c.col + 1);
        neighbors[4] = new Coordinate(c.row + 1, c.col - 1);
        neighbors[5] = new Coordinate(c.row + 1, c.col    );

        //remove illegal neighbors & neighbors of different color (very racist)
        int nullCount = 0;
        for (int iCount = 0; iCount < 6; iCount++) {
            Coordinate n = neighbors[iCount];
            if (n.row < 0 || n.row >= table.size
                    || n.col < 0 || n.col >= table.size
                    || table.matrix[n.row][n.col] != color) {
                neighbors[iCount] = null;
                nullCount++;
            }
        }

        //create a new array without null values and return that array
        Coordinate[] result = new Coordinate[6 - nullCount];
        int resultCount = 0;

        for (int iCount = 0; iCount < 6; iCount++) {
            Coordinate n = neighbors[iCount];
            if (n == null) {
                continue;
            }
            result[resultCount] = n;
            resultCount++;
        }

        return result;
    }
}