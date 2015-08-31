package edu.drexel.cs.jah473.autocorrect;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Comparator} to sort strings based on Levenshtein Edit Distance to a
 * central word. Prefix matches are favored. Ties are broken by sorting the
 * strings according to their natural order.
 * 
 * @author Justin Horvitz
 *
 */
public class LEDComparator implements Comparator<String> {
    protected Map<String, Integer> ledMemo = new HashMap<>();
    protected String word;
    protected char[] wordArr;

    /**
     * Constructs a new LED comparator using the given central word.
     * 
     * @param word
     *            the word to which LED is measured
     */
    public LEDComparator(String word) {
        this.word = word;
        wordArr = (" "+word).toCharArray();
    }

    protected int calcDist(String suggestion) {
        return Autocorrect.led(suggestion, word);
    }

    /**
     * Compares the LED of the two strings to the central word.
     */
    @Override
    public int compare(String str1, String str2) {
        if (str1.startsWith(word)) {
            if (!str2.startsWith(word)) {
                return -1;
            }
        } else if (str2.startsWith(word)) {
            return 1;
        }
        int dist1 = ledMemo.computeIfAbsent(str1, this::calcDist);
        int dist2 = ledMemo.computeIfAbsent(str2, this::calcDist);
        int c = Integer.compare(dist1, dist2);
        if (c != 0) {
            return c;
        }
        return str1.compareTo(str2);
    }
}
