package hex;

import java.util.Scanner;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerHuman implements Player{

    @Override
    public Coordinate makeMove(Table t) {
        Scanner scan = new Scanner(System.in);
        System.out.println(t);
        System.out.println("Row: ");
        int row = scan.nextInt();
        System.out.println("Col: ");
        int col = scan.nextInt();
        return new Coordinate(--row, --col);
    }
}
