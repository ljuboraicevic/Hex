/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hex;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nikola
 */
public class GameStats {

    /**
     * list of tables, after each move
     */
    private LinkedList<Table> moves;

    /**
     * 1-vertical player won 2-horizontal player won
     */
    private byte whoWon;

    /**
     * table dimension
     */
    private int tableSize;

    public GameStats(Game game) throws Exception {
        //if game is not finished
        if (game.whoWon() == 0) {
            throw new Exception("Game is not finished!");
        } else {
            this.whoWon = game.whoWon();
            this.moves = game.getAllMoves();
            this.tableSize = this.moves.element().size;
        }
    }

    /**
     * writes all moves to file and appends label to each move-line
     * @param trainingSetFile 
     */
    public void writeStatsToFile(String trainingSetFile) {
        try {
            FileOutputStream trainingOut = new FileOutputStream(trainingSetFile,true);
            for (Table move : this.moves) {
                byte[] bMove = toByteArray(move);
                trainingOut.write(bMove);
                if (this.whoWon == 1) {
                    trainingOut.write('1');
                } else {
                    trainingOut.write('0');
                }
                trainingOut.write('\n');
                trainingOut.write(changePlayersMarks(bMove));
                if (this.whoWon == 1) {
                    trainingOut.write('0');
                } else {
                    trainingOut.write('1');
                }
                trainingOut.write('\n');
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameStats.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(GameStats.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates byte array from table
     *
     * @param table
     * @return
     */
    static byte[] toByteArray(Table table) {
        byte[] bytes = new byte[table.size * table.size];
        int count = 0;
        for (int i = 0; i < table.size; i++) {
            for (int j = 0; j < table.size; j++) {
                bytes[count++] = (byte) (table.matrix[i][j] + 48);
            }
        }
        return bytes;
    }

    /**
     * Changes player's "perspective". fun([10010202]) = [20020101]
     *
     * @param move
     * @return
     */
    static byte[] changePlayersMarks(byte[] move) {
        byte[] result = Arrays.copyOf(move, move.length);
        for (int i = 0; i < result.length; i++) {
            if (result[i] == '1') {
                result[i] = '2';
            } else if (result[i] == '2') {
                result[i] = '1';
            }
        }
        return result;
    }

    /**
     * adds comma between bytes [100] => [1,0,0] ----------- not used
     * @param bytes
     * @return 
     */
    /*
    static byte[] formatForWriting(byte[] bytes) {
        byte[] result = new byte[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            result[2 * i] = (byte) (bytes[i] + 48);
            result[2 * i + 1] = ',';
        }
        return result;
    }
    */
}
