package edu.drexel.cs.jah473.autocorrect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.drexel.cs.jah473.autocorrect.Autocorrect;
import edu.drexel.cs.jah473.util.Stats;
import edu.drexel.cs.jah473.util.Strings;

/*
 * JUnit Tests for Autocorrect class. All 8 tests passed in 18.4 seconds.
 * Code coverage of Autocorrect.java was 100%.
 */

public class AutocorrectTest {

    private static Autocorrect ac;
    private static final int MAX_WORD_LENGTH = 10;
    private static final int NUM_TRIALS = 1;
    private static final int NUM_WORDS = 10;
    private static final Random RAND = new Random();
    private static Set<String> words = new HashSet<>(NUM_WORDS);

    private static List<String> naiveAutocomplete(String prefix) {
        return words.parallelStream().filter(w -> w.startsWith(prefix)).collect(Collectors.toList());
    }

    private static int naiveLED(String s, String t) {
        final int lenS = s.length();
        final int lenT = t.length();

        if (lenS == 0) {
            return lenT;
        }
        if (lenT == 0) {
            return lenS;
        }
        int cost = 0;
        if (Strings.lastChar(s) != Strings.lastChar(t)) {
            cost = 1;
        }
        String subS = s.substring(0, lenS - 1);
        String subT = t.substring(0, lenT - 1);
        return Stats.min(naiveLED(subS, t) + 1, naiveLED(s, subT) + 1, naiveLED(subS, subT) + cost);

    }

    private static List<String> naiveSuggest(String word) {
        Set<String> suggestionsSet = new HashSet<>();
        if (words.contains(word)) {
            suggestionsSet.add(word);
        }
        if (ac.useAutocomplete) {
            suggestionsSet.addAll(naiveAutocomplete(word));
        }
        if (ac.useLED) {
            suggestionsSet.addAll(naiveSuggestLED(word, ac.getMaxLED()));
        }
        if (ac.useWhitespace) {
            suggestionsSet.addAll(naiveSuggestWhitespace(word));
        }
        Comparator<? super String> comparator = null;
        switch (ac.getSortMode()) {
        case Autocorrect.KEYBOARD_DISTANCE_SORT:
            comparator = new KeyboardDistanceComparator(word);
            break;
        case Autocorrect.LED_SORT:
            comparator = new LEDComparator(word);
            break;
        case Autocorrect.CUSTOM_SORT:
            comparator = ac.getComparator();
            break;
        }
        List<String> suggestions = new ArrayList<>(suggestionsSet);
        if (comparator != null) {
            Collections.sort(suggestions, comparator);
        }
        return suggestions;
    }

    private static List<String> naiveSuggestLED(String word, int maxLED) {
        return words.parallelStream().filter(w -> Autocorrect.led(w, word) <= maxLED).collect(Collectors.toList());
    }

    private static List<String> naiveSuggestWhitespace(String word) {
        List<String> suggestions = new ArrayList<>();
        final int len = word.length();
        for (int i = 1; i < len; i++) {
            String part1 = word.substring(0, i);
            String part2 = word.substring(i, len);
            if (words.contains(part1) && words.contains(part2)) {
                suggestions.add(part1 + ' ' + part2);
            }
        }
        return suggestions;
    }

    private static char randChar() {
        return (char) (RAND.nextInt(26) + 'a');
    }

    private static String randWord() {
        StringBuilder sb = new StringBuilder();
        int len = RAND.nextInt(MAX_WORD_LENGTH) + 1;
        for (int i = 0; i < len; i++) {
            sb.append(randChar());
        }
        return sb.toString();
    }

    @BeforeClass
    public static void setup() {
        for (int i = 0; i < NUM_WORDS; i++) {
            String word = randWord();
            words.add(word);
        }
        ac = new Autocorrect(words, false);
    }

    @Test
    public void altConstructorTest() {
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(s1.hashCode(), s2.hashCode());
            }

        };
        Autocorrect ac2 = new Autocorrect(words, false, comparator);
        assertEquals(Autocorrect.CUSTOM_SORT, ac2.getSortMode());
        for (int i = 0; i < NUM_TRIALS; i++) {
            String word = randWord();
            List<String> suggestions = ac2.suggest(word);
            int len = suggestions.size();
            for (int j = 1; j < len; j++) {
                String s1 = suggestions.get(j - 1);
                String s2 = suggestions.get(j);
                assertTrue(comparator.compare(s1, s2) <= 0);
            }
        }
        final int numSortModes = 4;
        List<String> empty = new ArrayList<>();
        for (int i = 0; i < NUM_TRIALS; i++) {
            int sortMode = RAND.nextInt(numSortModes);
            Autocorrect ac3 = new Autocorrect(empty, false, sortMode);
            assertEquals(sortMode, ac3.getSortMode());
        }

    }

    @Test
    public void autocompleteTest() {
        for (int i = 0; i < NUM_TRIALS; i++) {
            String prefix = randWord();
            List<String> naiveSays = naiveAutocomplete(prefix);
            List<String> acSays = ac.autocomplete(prefix);
            Collections.sort(naiveSays);
            Collections.sort(acSays);
            assertEquals(naiveSays, acSays);
        }
    }

    @Test
    public void customComparatorTest() {
        Comparator<String> comparator = new Comparator<String>() {

            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(s1.length(), s2.length());
            }
        };
        ac.setComparator(comparator);
        ac.setSortMode(Autocorrect.CUSTOM_SORT);
        for (int i = 0; i < NUM_TRIALS; i++) {
            String word = randWord();
            List<String> suggestions = ac.suggest(word);
            int len = suggestions.size();
            for (int j = 1; j < len; j++) {
                String s1 = suggestions.get(j - 1);
                String s2 = suggestions.get(j);
                assertTrue(comparator.compare(s1, s2) <= 0);
            }
        }
    }

    @Test
    public void errorConditionsTest() {
        boolean caught = false;
        try {
            ac.suggestLED(randWord(), -1);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);
        caught = false;
        try {
            ac.setMaxLED(-1);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);
        caught = false;
        try {
            ac.setSortMode(-1);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);
        caught = false;
        try {
            ac.setSortMode(20);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);
        caught = false;
    }

    @Test
    public void ledTest() {
        for (int i = 0; i < NUM_TRIALS; i++) {
            String s1 = randWord();
            String s2 = randWord();
            int naiveSays = naiveLED(s1, s2);
            int acSays = Autocorrect.led(s1, s2);
            assertEquals(naiveSays, acSays);
        }
    }

    @Test
    public void suggestLEDTest() {
        final int maxMaxLED = 3;
        for (int i = 0; i < NUM_TRIALS; i++) {
            String word = randWord();
            int maxLED = RAND.nextInt(maxMaxLED + 1);
            List<String> naiveSays = naiveSuggestLED(word, maxLED);
            List<String> acSays = ac.suggestLED(word, maxLED);
            Collections.sort(naiveSays);
            Collections.sort(acSays);
            assertEquals(naiveSays, acSays);
        }
        String word = randWord();
        List<String> naiveSays = naiveSuggestLED(word, ac.getMaxLED());
        List<String> acSays = ac.suggestLED(word);
        Collections.sort(naiveSays);
        Collections.sort(acSays);
        assertEquals(naiveSays, acSays);
        int maxLED;
        do {
            maxLED = RAND.nextInt(maxMaxLED + 1);
        } while (maxLED == ac.getMaxLED());
        ac.setMaxLED(maxLED);
        naiveSays = naiveSuggestLED(word, ac.getMaxLED());
        acSays = ac.suggestLED(word);
        Collections.sort(naiveSays);
        Collections.sort(acSays);
        assertEquals(naiveSays, acSays);
    }

    @Test
    public void suggestTest() {
        final int numSortModes = 4;
        for (int i = 0; i < NUM_TRIALS; i++) {
            ac.useAutocomplete = RAND.nextBoolean();
            ac.useLED = RAND.nextBoolean();
            ac.useWhitespace = RAND.nextBoolean();
            ac.setSortMode(RAND.nextInt(numSortModes));
            String word = randWord();
            List<String> naiveSays = naiveSuggest(word);
            List<String> acSays = ac.suggest(word);
            if (ac.getComparator() == null) {
                Collections.sort(naiveSays);
                Collections.sort(acSays);
            } else if (ac.contains(word)) {
                assertEquals(acSays.get(0), word);
            }
            assertEquals(naiveSays, acSays);
        }
    }

    @Test
    public void suggestWhitespaceTest() {
        for (int i = 0; i < NUM_TRIALS; i++) {
            String word = randWord();
            List<String> naiveSays = naiveSuggestWhitespace(word);
            List<String> acSays = ac.suggestWhitespace(word);
            Collections.sort(naiveSays);
            Collections.sort(acSays);
            assertEquals(naiveSays, acSays);
        }
    }

    @Test
    public void keyboardDistTest() {
        List<String> corpus = Arrays.asList(new String[] { "toweq", "tower", "towel", "towen" });
        Autocorrect a = new Autocorrect(corpus, false);
        List<String> suggestions = a.suggest("towek");
        assertEquals("towel", suggestions.get(0));
    }

}
