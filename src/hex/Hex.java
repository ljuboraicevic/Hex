package hex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class Hex {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
//        playRegularGames(
//                new PlayerMonteCarlo(1500, 2), //first player
//                new PlayerMonteCarlo(1500, 2), //second player
//                11,                            //table size
//                5);                            //number of games
        
        playMonteCarloRecordedGames(
                1500,              //first player Monte Carlo repetitions
                1500,              //second player Monte Carlo repetitions
                11,                //table size
                1,                 //number of games
                2,                 //paralelization
                "TrainingSet1");   //file
    }
    
    private static void playRegularGames(
            Player first, 
            Player second, 
            int tableSize, 
            int noOfGames) {
        
        for (int iCount = 0; iCount < noOfGames; iCount++) {
            Table table = new Table(tableSize);
            Game g = new Game(table, first, second);
            g.play();
        }
    }

    private static void playMonteCarloRecordedGames(
            int MCRepetitionsPlayer1, 
            int MCRepetitionsPlayer2, 
            int tableSize,
            int noOfGames,
            int paralelization, 
            String file) throws IOException {
        
        try (Writer writer = new BufferedWriter(new FileWriter(new File(file)))) {
            for (int iCount = 0; iCount < noOfGames; iCount++) {
                Table table = new Table(tableSize);
                PlayerMonteCarlo first = new PlayerMonteCarlo(MCRepetitionsPlayer1, paralelization);
                PlayerMonteCarlo second = new PlayerMonteCarlo(MCRepetitionsPlayer2, paralelization);
                GameRecordedMonteCarlo g = new GameRecordedMonteCarlo(table, first, second, MCRepetitionsPlayer1 / 10);
                g.play();
                writer.write(g.gameStats());
            }
            writer.flush();
        }
    }
}
