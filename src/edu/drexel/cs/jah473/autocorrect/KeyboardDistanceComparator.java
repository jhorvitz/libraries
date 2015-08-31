package edu.drexel.cs.jah473.autocorrect;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.IntStream;

import edu.drexel.cs.jah473.distance.DistanceFunction;
import edu.drexel.cs.jah473.distance.KDPoint;
import edu.drexel.cs.jah473.util.Pair;

/**
 * A {@link Comparator} that weights LED by distance from the typed letter to
 * the correct letter on a standard QWERTY keyboard. Prefix matches are favored.
 * Ties are broken by sorting the strings according to their natural order.
 * 
 * @author Justin Horvitz
 *
 */
public class KeyboardDistanceComparator extends LEDComparator implements Comparator<String> {
    protected static final KDPoint DEFAULT_POINT = new KDPoint(2, 4);

    @SuppressWarnings("serial")
    protected static final Map<Character, KDPoint> KEYBOARD = new HashMap<Character, KDPoint>() {
        {
            String alphabet = "1234567890qwertyuiopasdfghjkl;zxcvbnm,./";
            int len = alphabet.length();
            for (int i = 0; i < len; i++) {
                put(alphabet.charAt(i), new KDPoint(i / KEYBOARD_WIDTH, i % KEYBOARD_WIDTH));
            }
            put(' ', new KDPoint(4, 4));
            String topRow = "!@#$%^&*()";
            len = topRow.length();
            for (int i = 0; i < len; i++) {
                put(topRow.charAt(i), get(alphabet.charAt(i)));
            }
            KDPoint point = new KDPoint(0, 10);
            put('-', point);
            put('_', point);
            point = new KDPoint(0, 11);
            put('=', point);
            put('+', point);
            point = new KDPoint(1, 10);
            put('[', point);
            put('{', point);
            point = new KDPoint(1, 11);
            put(']', point);
            put('}', point);
            point = new KDPoint(1, 12);
            put('\\', point);
            put('|', point);
            point = new KDPoint(2, 10);
            put('\'', point);
            put('"', point);
        }
    };
    protected static final Set<Character> VOWELS = new HashSet<>();
    static {
        VOWELS.add('a');
        VOWELS.add('e');
        VOWELS.add('i');
        VOWELS.add('o');
        VOWELS.add('u');
        VOWELS.add('A');
        VOWELS.add('E');
        VOWELS.add('I');
        VOWELS.add('O');
        VOWELS.add('U');
    }
    protected static final int KEYBOARD_WIDTH = 10;
    protected static final DistanceFunction DIST = (a, b) -> 2 * Math.abs(a.getCoord(0) - b.getCoord(0))
            + Math.abs(a.getCoord(1) - b.getCoord(1));

    protected static int kDist(char c1, char c2) {
        return (int) DIST.distanceBetween(KEYBOARD.getOrDefault(Character.toLowerCase(c1), DEFAULT_POINT),
                KEYBOARD.getOrDefault(Character.toLowerCase(c2), DEFAULT_POINT));
    }

    /**
     * Constructs a new keyboard distance comparator using the given central
     * word.
     * 
     * @param word
     *            the word to which weighted LED is measured
     */
    public KeyboardDistanceComparator(String word) {
        super(word);
    }

    @Override
    protected int calcDist(String suggestion) {
        return keyboardLED(suggestion);
    }

    protected int keyboardLED(String s) {
        char[] compArr = (" " + s).toCharArray();
        int len1 = wordArr.length;
        int len2 = compArr.length;
        int[][] matrix = new int[len2][len1];
        IntStream.range(0, len2).forEach(i -> matrix[i][0] = i);
        IntStream.range(0, len1).forEach(i -> matrix[0][i] = i);
        Map<Pair<Integer, Integer>, Integer> penalties = new HashMap<>();
        for (int i = 1; i < len2; i++) {
            for (int j = 1; j < len1; j++) {
                matrix[i][j] = matrix[i - 1][j - 1];
                if (compArr[i] != wordArr[j]) {
                    matrix[i][j] = Math.min(matrix[i - 1][j], matrix[i][j - 1]) + 1;
                    if (matrix[i - 1][j - 1] + 1 < matrix[i][j]) {
                        matrix[i][j] = matrix[i - 1][j - 1] + 1;
                        int dist = kDist(compArr[i], wordArr[j]);
                        if (Character.isLowerCase(compArr[i]) != Character.isLowerCase(wordArr[j])) {
                            dist++;
                        }
                        if (VOWELS.contains(compArr[i]) != VOWELS.contains(wordArr[j])) {
                            dist++;
                        }
                        penalties.put(new Pair<>(i, j), dist);
                    }
                }
            }
        }
        Pair<Integer, Integer> loc = new Pair<>(len2 - 1, len1 - 1);
        int led = matrix[len2 - 1][len1 - 1];
        int wDist = 0;
        while (loc.fst > 0 && loc.snd > 0) {
            Integer penalty = penalties.get(loc);
            if (penalty != null) {
                wDist += penalty;
                led--;
            }
            if (compArr[loc.fst] == wordArr[loc.snd]) {
                loc.fst --;
                loc.snd--;
                continue;
            }
            int ul, u, l;
            Pair<Integer, Integer> upleft = new Pair<>(loc.fst - 1, loc.snd - 1);
            Pair<Integer, Integer> up = new Pair<>(loc.fst - 1, loc.snd);
            Pair<Integer, Integer> left = new Pair<>(loc.fst, loc.snd - 1);
            try {
                ul = matrix[upleft.fst][upleft.snd] - penalties.getOrDefault(upleft, 0);
            } catch (ArrayIndexOutOfBoundsException e) {
                ul = Integer.MAX_VALUE;
            }
            try {
                u = matrix[up.fst][up.snd] - penalties.getOrDefault(up, 0);
            } catch (ArrayIndexOutOfBoundsException e) {
                u = Integer.MAX_VALUE;
            }
            try {
                l = matrix[left.fst][left.snd] - penalties.getOrDefault(left, 0);
            } catch (ArrayIndexOutOfBoundsException e) {
                l = Integer.MAX_VALUE;
            }
            if (ul <= u) {
                if (ul <= l) {
                    loc.fst--;
                }
                loc.snd--;
            } else {
                if (u < l) {
                    loc.fst--;
                } else {
                    loc.snd--;
                }
            }
        }
        return wDist + 4 * led;
    }
}