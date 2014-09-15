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
        playRegularGames(
                new PlayerNeuralNetwork("1.nnet"),       //first player
                new PlayerMonteCarlo(10, 2),       //second player
                11,                                 //board size
                10);                                //number of games
        
//        playMonteCarloRecordedGames(
//                1500,              //first player Monte Carlo repetitions
//                1500,              //second player Monte Carlo repetitions
//                11,                //board size
//                15,                 //number of games
//                2,                 //paralelization
//                "TrainingSet1");   //file
    }
    
    private static void playRegularGames(
            Player first, 
            Player second, 
            int boardSize, 
            int noOfGames) {
        
        for (int iCount = 0; iCount < noOfGames; iCount++) {
            Board board = new Board(boardSize);
            Game g = new Game(board, first, second);
            g.play();
        }
    }

    private static void playMonteCarloRecordedGames(
            int MCRepetitionsPlayer1, 
            int MCRepetitionsPlayer2, 
            int boardSize,
            int noOfGames,
            int paralelization, 
            String file) throws IOException {
        
        try (Writer writer = new BufferedWriter(new FileWriter(new File(file)))) {
            for (int iCount = 0; iCount < noOfGames; iCount++) {
                Board board = new Board(boardSize);
                PlayerMonteCarlo first = new PlayerMonteCarlo(MCRepetitionsPlayer1, paralelization);
                PlayerMonteCarlo second = new PlayerMonteCarlo(MCRepetitionsPlayer2, paralelization);
                GameRecordedMonteCarlo g = new GameRecordedMonteCarlo(board, first, second, MCRepetitionsPlayer1);
                g.play();
                writer.write(g.gameStats());
            }
            writer.flush();
        }
    }
}
