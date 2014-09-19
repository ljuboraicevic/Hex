
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hex;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author nikola
 */
public class SpecialFrequencyTable {

    private ArrayList<PlayerMonteCarlo> players;
    private ArrayList<Integer> frequencies;
    private int totalSize;

    public SpecialFrequencyTable(LinkedList<PlayerMonteCarlo> players, LinkedList<Integer> frequencies) {
        assert (players.size() == frequencies.size());
        totalSize = 0;
        this.players = new ArrayList<>();
        this.frequencies = new ArrayList<>();
        while (!players.isEmpty() && !frequencies.isEmpty()) {
            PlayerMonteCarlo pmc = players.removeFirst();
            int fr = frequencies.removeFirst();
            totalSize += fr;

            if (this.players.contains(players.peekFirst())) {
                int index = this.players.indexOf(pmc);
                this.frequencies.set(index, this.frequencies.get(index) + fr);
            } else {
                this.players.add(pmc);
                this.frequencies.add(fr);
            }
        }
    }

    public PlayerMonteCarlo getPlayer() {
        int random = (int) Math.ceil(Math.random() * totalSize);
        int index = 0;
        int cummulativeSum = 0;
        while (cummulativeSum + frequencies.get(index) < random) {
            cummulativeSum += frequencies.get(index);
            index++;
        }
        return this.players.get(index);
    }
}
