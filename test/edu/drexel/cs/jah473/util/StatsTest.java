package edu.drexel.cs.jah473.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatsTest {
    @Test
    public void minTest() {
        int min = Stats.min(1, 2, 0, 3, -4, 5, -2);
        assertEquals(-4, min);
    }

    @Test
    public void maxTest() {
        int max = Stats.max(1, 2, 0, 3, -4, 5, -2);
        assertEquals(5, max);
    }

    @Test
    public void meanTest() {
        double mean = Stats.mean(1, 7, 5, 12, 3, 8);
        assertEquals(6, mean, 0);
    }

    @Test
    public void kSmallestLargestTest() {
        List<Integer> list = Arrays.asList(new Integer[] { 1, 2, 4, 8, 0, 3, 9 });
        List<Integer> small = Stats.kSmallest(list, 2);
        assertEquals(2, small.size());
        assertEquals(0, (int) small.get(0));
        assertEquals(1, (int) small.get(1));
        List<Integer> large = Stats.kLargest(list, 2);
        assertEquals(2, large.size());
        assertEquals(9, (int) large.get(0));
        assertEquals(8, (int) large.get(1));

    }
}
