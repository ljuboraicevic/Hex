package hex;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class Game {
    Table table;
    Player[] players;
    int movesPlayed;

    public Game(Table t, Player first, Player second) {
        this.movesPlayed = 0;
        this.players = new Player[2];
        this.players[0] = first;
        this.players[1] = second;
        this.table = t;
    }
    
    public boolean isFinished() {
        return table.noOfEmptyFields == 0;
        //TODO implement isFinished
    }
    
    public void play() {
        while (!isFinished()) {
            Coordinate move = players[movesPlayed % 2].makeMove(table);
            table.putMark(move, (byte) (movesPlayed % 2 + 1));
            movesPlayed++;
        }
    }
}
