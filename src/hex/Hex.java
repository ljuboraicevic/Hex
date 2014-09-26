package hex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

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
              int i = 0;while(i++<100){ playRegularGames(
                new PlayerNeuralNetwork("normalized.nnet"),       //second player
                                       new PlayerNeuralNetwork("notNormalized.nnet"),       //first player

                7,                                 //board size
                1);                                //number of games
              }
//        playMonteCarloRecordedGames(
//                10000,               //first player Monte Carlo repetitions
//                100,                  //second player Monte Carlo repetitions
//                7,                  //board size
//                30,                  //number of games
//                2,                  //paralelization
//                "TrainingSetNonNormalized",     //file
//                true,               //randomize best
//                true,               //record only first players move
//                false                //normalize probabilities
//        );   
////        
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
            String file,
            boolean recordOnlyFirstPlayerMoves,
            boolean normalizeProbabilities,
            boolean randomBest) throws IOException {
        
        try (Writer writer = new BufferedWriter(new FileWriter(new File(file),true))) {
            for (int iCount = 0; iCount < noOfGames; iCount++) {
                Board board = new Board(boardSize);
                PlayerMonteCarlo first = new PlayerMonteCarlo(MCRepetitionsPlayer1, paralelization, randomBest);
                PlayerMonteCarlo second = new PlayerMonteCarlo(MCRepetitionsPlayer2, paralelization, randomBest);
                GameRecordedMonteCarlo g = new GameRecordedMonteCarlo(board, first, second, normalizeProbabilities);
                try {
                    g.play();
                //    writer.write(g.gameStats());
                    writer.write(g.additionalGameStats(recordOnlyFirstPlayerMoves));
                } catch (NullPointerException e){
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
            writer.flush();
        }
    }
    
    private static void playMonteCarloRandomRecordedGames(String file,
            int boardSize,
            int noOfGames,
            int paralelization,
            boolean recordOnlyFirstPlayerMoves,
            int numberOfPlayers,
            int...args) throws IOException {
        
        LinkedList<PlayerMonteCarlo> players = new LinkedList<PlayerMonteCarlo>();
        LinkedList<Integer> frequencies = new LinkedList<Integer>();

        HashMap<Integer,Integer> counter = new HashMap<>();
        
        for(int i = 0; i < numberOfPlayers; i++){
            players.add(new PlayerMonteCarlo(args[i * 2], paralelization,false));
            frequencies.add(args[i * 2 + 1]);
            counter.put(args[i * 2], 0);
        }
        
        SpecialFrequencyTable ft = new SpecialFrequencyTable(players, frequencies);
        
        try (Writer writer = new BufferedWriter(new FileWriter(new File(file),true))) {
            for (int iCount = 0; iCount < noOfGames; iCount++) {
                Board board = new Board(boardSize);
                PlayerMonteCarlo first = ft.getPlayer();
                PlayerMonteCarlo second = ft.getPlayer();
                counter.put(first.getNumberOfRepetitions(),
                        counter.get(first.getNumberOfRepetitions()) + 1);
                counter.put(second.getNumberOfRepetitions(),
                        counter.get(second.getNumberOfRepetitions()) + 1);
                GameRecordedMonteCarlo g = new GameRecordedMonteCarlo(board, 
                        first, second, false);
                System.out.println("Starting game "+(iCount+1)+":\n"+
                        "Player1 (MonteCarlo) "+ first.getNumberOfRepetitions()+
                        "\nPlayer2 (MonteCarlo) "+ second.getNumberOfRepetitions());
                try {
                    g.play();
            //        writer.write(g.gameStats());
                    writer.write(g.additionalGameStats(recordOnlyFirstPlayerMoves));

                } catch (NullPointerException e){
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
                
            }
            writer.flush();
        }
        System.out.println("Games per player:");
        for(Entry<Integer, Integer> e : counter.entrySet()){
            System.out.println(e.getKey() + " - " + e.getValue());
        }
    }
}
