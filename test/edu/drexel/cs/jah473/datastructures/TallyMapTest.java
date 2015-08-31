package edu.drexel.cs.jah473.datastructures;

import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Test;

public class TallyMapTest {

    static final int RANGE = 100;
    static final int NUM_TRIALS = 100;
    static final int NUM_INCREMENTS = 1_000_000;
    static final Random RAND = new Random();

    @Test
    public void tallyTest() {
        for (int i = 0; i < NUM_TRIALS; i++) {
            TallyMap<Integer> tm = new TallyMap<>();
            int[] counts = new int[RANGE];
            for (int j = 0; j < NUM_INCREMENTS; j++) {
                int rand = RAND.nextInt(RANGE);
                tm.increment(rand);
                counts[rand]++;
            }
            for (int j = 0; j < RANGE; j++) {
                assertEquals(counts[j], tm.getTally(j));
            }
            int max = counts[0];
            for (int count : counts) {
                max = Math.max(max, count);
            }
            assertEquals(max, tm.getMaxTally());
        }
    }
}
