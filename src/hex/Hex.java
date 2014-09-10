package hex;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class Hex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int i = 0;
        while (i++ < 10) {
            Table table = new Table(11);
            Player first = new PlayerMonteCarlo(1000);
            Player second = new PlayerMonteCarlo(1000);

            Game g = new Game(table, first, second);
            g.play();

            try {
                GameStats stat = new GameStats(g);
                stat.writeStatsToFile("trainingSet.txt");
            } catch (Exception ex) {
                Logger.getLogger(Hex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
