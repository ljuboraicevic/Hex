package hex;

import java.util.Scanner;

/**
 * Class PlayerHuman implements Player interface. It is used to input moves from
 * a human player.
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerHuman implements Player{

    /**
     * Asks player to input the coordinates of her move and returns that as her
     * move.
     * 
     * @param t Hex table
     * @return Coordinates of the player's move
     */
    @Override
    public Coordinate makeMove(Table t) {
        Scanner scan = new Scanner(System.in);
        System.out.println(t);
        Coordinate move = null;
        boolean legal = false;
        
        while (!legal) {
            System.out.print("Row: ");
            int row = scan.nextInt();
            System.out.print("Col: ");
            int col = scan.nextInt();
            move = new Coordinate(--row, --col);
            legal = t.isMoveLegal(move);
            if (!legal) { System.out.println("Illegal move!"); }
        }
        
        return move;
    }
}
