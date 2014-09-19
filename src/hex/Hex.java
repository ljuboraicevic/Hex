package hex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

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
//                new PlayerNeuralNetwork("1.nnet"),       //first player
//                new PlayerMonteCarlo(10, 2),       //second player
//                11,                                 //board size
//                10);                                //number of games
        
//        playMonteCarloRecordedGames(
//                1500,              //first player Monte Carlo repetitions
//                1500,              //second player Monte Carlo repetitions
//                11,                //board size
//                15,                 //number of games
//                2,                 //paralelization
//                "TrainingSet1");   //file
        
        playMonteCarloRandomRecordedGames(
                "TrainingSet1",     //file
                7,                  //board size
                1000,                 //number of games
                2,                  //paralelization
                5,                  //number of players
                20, 10,             // for each player number of repetitions, frequency
                100, 15,            
                500, 10,
                1000, 30,
                5000, 5
        );
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
    
    private static void playMonteCarloRandomRecordedGames(String file,
            int boardSize,
            int noOfGames,
            int paralelization,
            int numberOfPlayers,
            int...args) throws IOException {
        
        LinkedList<PlayerMonteCarlo> players = new LinkedList<PlayerMonteCarlo>();
        LinkedList<Integer> frequencies = new LinkedList<Integer>();

        for(int i = 0; i < numberOfPlayers; i++){
            players.add(new PlayerMonteCarlo(args[i * 2], paralelization));
            frequencies.add(args[i * 2 + 1]);
        }
        
        SpecialFrequencyTable ft = new SpecialFrequencyTable(players, frequencies);
        
        try (Writer writer = new BufferedWriter(new FileWriter(new File(file),true))) {
            for (int iCount = 0; iCount < noOfGames; iCount++) {
                Board board = new Board(boardSize);
                PlayerMonteCarlo first = ft.getPlayer();
                PlayerMonteCarlo second = ft.getPlayer();
                GameRecordedMonteCarlo g = new GameRecordedMonteCarlo(board, 
                        first, second, first.getNumberOfRepetitions());
                System.out.println("Starting game "+(iCount+1)+":\n"+
                        "Player1 (MonteCarlo) "+ first.getNumberOfRepetitions()+
                        "\nPlayer2 (MonteCarlo) "+ second.getNumberOfRepetitions());
                g.play();
                writer.write(g.gameStats());
            }
            writer.flush();
        }
    }
}
