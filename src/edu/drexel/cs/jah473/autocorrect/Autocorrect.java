package edu.drexel.cs.jah473.autocorrect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;

import edu.drexel.cs.jah473.datastructures.Trie;
import edu.drexel.cs.jah473.util.Stats;
import edu.drexel.cs.jah473.util.Strings;

/**
 * Class for generating suggestions for misspelled words. Suggestions are taken
 * from a user-defined corpus of valid words. The class contains three main
 * algorithms for generating suggestions: {@linkplain #autocomplete(String)
 * autocomplete}, {@linkplain #suggestLED(String) Levenshtein Edit Distance},
 * and {@linkplain #suggestWhitespace(String) whitespace}.<br>
 * <br>
 * In addition to generating suggestions using the three algorithms
 * individually, the user can generate suggestions using any combination (or
 * all) of the algorithms via the {@linkplain #suggest(String) suggest}
 * function.<br>
 * <br>
 * The user has four options for automatically sorting suggestions that come as
 * a result of the {@linkplain #suggest(String) suggest} function. The default
 * is {@linkplain #KEYBOARD_DISTANCE_SORT keyboard distance sort}, which uses
 * the {@link KeyboardDistanceComparator} class to rank suggestions based on the
 * keyboard distance between the typo(s) in the misspelled word and the
 * corresponding character(s) in the suggestion. This method produces extremely
 * relevant results and is highly recommended. Alternatively, the user can
 * choose {@linkplain #LED_SORT LED sort mode} to sort using an
 * {@link LEDComparator}, {@linkplain #CUSTOM_SORT custom sort mode} along with
 * a user-provided comparator, or {@linkplain #UNSORTED unsorted mode}.<br>
 * <br>
 * Post-construction changes to the corpus may be made via the dynamic
 * operations in the {@link Trie} class.
 * 
 * @author Justin Horvitz
 *
 */
public class Autocorrect extends Trie {
    /**
     * A constant representing custom sort mode. The integral value of this
     * constant is {@value #CUSTOM_SORT}.
     */
    public static final int CUSTOM_SORT = 2;
    /**
     * A constant representing sort by keyboard distance mode (the default
     * mode). The integral value of this constant is
     * {@value #KEYBOARD_DISTANCE_SORT}.
     */
    public static final int KEYBOARD_DISTANCE_SORT = 0;
    /**
     * A constant representing LED sort mode. The integral value of this
     * constant is {@value #LED_SORT}.
     */
    public static final int LED_SORT = 1;

    protected static final String NEGATIVE_LED_MESSAGE = "maximum LED cannot be negative";

    protected static final int NUM_SORT_MODES = 4;
    private static final long serialVersionUID = -7639825226416223971L;
    /**
     * A constant representing unsorted suggestions mode. The integral value of
     * this constant is {@value #UNSORTED}.
     */
    public static final int UNSORTED = 3;

    /**
     * Calculates the Levenshtein Edit Distance (LED) between two words
     * 
     * @param word1
     *            the first word
     * @param word2
     *            the second word
     * @return the LED between the two words
     */
    public static int led(String word1, String word2) {
        char[] wordArr1 = (' ' + word1).toCharArray();
        char[] wordArr2 = (' ' + word2).toCharArray();
        int[][] matrix = new int[wordArr1.length][wordArr2.length];
        IntStream.range(1, matrix.length).forEach(i -> matrix[i][0] = i);
        IntStream.range(1, matrix[0].length).forEach(j -> matrix[0][j] = j);
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                matrix[i][j] = matrix[i - 1][j - 1];
                if (wordArr1[i] != wordArr2[j]) {
                    matrix[i][j] = Stats.min(matrix[i][j], matrix[i - 1][j], matrix[i][j - 1]) + 1;
                }
            }
        }
        return matrix[word1.length()][word2.length()];
    }

    /* Recursive helper function for LED suggestions */
    private static void suggestLEDAux(TrieNode node, char[] letters, int maxLED, StringBuilder compWord,
            int[][] matrix, List<String> suggestions) {
        if (compWord.length() > matrix.length) {
            return;
        }
        int length = letters.length;
        int i = compWord.length() - 1;
        matrix[i][0] = i;
        int minVal = i;
        for (int j = 1; j < length; j++) {
            matrix[i][j] = matrix[i - 1][j - 1];
            if (compWord.charAt(i) != letters[j]) {
                matrix[i][j] = Stats.min(matrix[i][j], matrix[i - 1][j], matrix[i][j - 1]) + 1;
            }
            if (matrix[i][j] < minVal) {
                minVal = matrix[i][j];
            }
        }
        if (matrix[compWord.length() - 1][length - 1] <= maxLED && node.isWord()) {
            suggestions.add(compWord.toString().substring(1));
        }
        if (minVal <= maxLED) {
            for (Entry<Character, TrieNode> e : node.entrySet()) {
                compWord.append(e.getKey());
                suggestLEDAux(e.getValue(), letters, maxLED, compWord, matrix, suggestions);
                Strings.pop(compWord);
            }
        }
    }

    protected Comparator<? super String> comparator;
    protected int maxLED = 3;
    protected int sortMode = KEYBOARD_DISTANCE_SORT;

    /**
     * Whether to generate suggestions using autocomplete, {@code true} by
     * default.
     */
    public boolean useAutocomplete = true;
    /**
     * Whether to generate suggestions using LED, {@code true} by default.
     */
    public boolean useLED = true;

    /**
     * Whether to generate suggestions using whitespace splitting, {@code true}
     * by default.
     */
    public boolean useWhitespace = true;

    /**
     * Constructs a new {@code Autocorrect} object using the given corpus.
     * Future changes to the given corpus will not affect this autocorrect.
     * 
     * @param corpus
     *            the words to be considered correctly spelled words
     * @param strictCaps
     *            whether to treat capital letters as unique
     */
    public Autocorrect(Collection<String> corpus, boolean strictCaps) {
        super(corpus, strictCaps);
    }

    /**
     * Constructs a new {@code Autocorrect} object using the given corpus and
     * comparator.
     * 
     * @param corpus
     *            the words to be considered correctly spelled words
     * @param strictCaps
     *            whether to treat capital letters as unique
     * @param comparator
     *            the comparator to be used for custom sorting, which will be
     *            turned on
     */
    public Autocorrect(Collection<String> corpus, boolean strictCaps, Comparator<? super String> comparator) {
        this(corpus, strictCaps);
        sortMode = CUSTOM_SORT;
        this.comparator = comparator;
    }

    /**
     * Constructs a new {@code Autocorrect} object using the given corpus and
     * sort mode. If the given sort mode is {@linkplain #CUSTOM_SORT custom sort
     * mode}, suggestions will remain unsorted until a comparator is set via
     * {@link #setComparator(Comparator)}.
     * 
     * @param corpus
     *            the words to be considered correctly spelled words
     * @param strictCaps
     *            whether to treat capital letters as unique
     * @param sortMode
     *            the sorting mode to use when generating suggestions
     */
    public Autocorrect(Collection<String> corpus, boolean strictCaps, int sortMode) {
        this(corpus, strictCaps);
        this.sortMode = sortMode;
    }

    /**
     * Generates a list of all words in the corpus that begin with the given
     * prefix.
     * 
     * @param prefix
     *            the prefix to autocomplete
     * @return an unsorted list of all words in the corpus that begin with the
     *         given prefix
     */
    public List<String> autocomplete(String prefix) {
        List<String> suggestions = new ArrayList<String>();
        TrieNode node = findNode(prefix);
        if (node == null) {
            return suggestions;
        }
        Iterator<String> iterator = new TrieIterator(node, prefix);
        while (iterator.hasNext()) {
            suggestions.add(iterator.next());
        }
        return suggestions;
    }

    /**
     * Returns the comparator being used by this autocorrect when it is in
     * {@linkplain Autocorrect#CUSTOM_SORT custom sort mode}.
     * 
     * @return the comparator being used by this autocorrect
     */
    public Comparator<? super String> getComparator() {
        return comparator;
    }

    /**
     * Gets the current maximum LED allowed.
     * 
     * @return the current maximum LED allowed
     */
    public int getMaxLED() {
        return maxLED;
    }

    /**
     * Returns the sort mode being used by this autocorrect. The pre-defined
     * default sort mode is {@linkplain Autocorrect#KEYBOARD_DISTANCE_SORT
     * keyboard distance sort mode}.
     * 
     * @return the integral value of the sort mode being used by this
     *         autocorrect
     */
    public int getSortMode() {
        return sortMode;
    }

    /**
     * Sets this autocorrect to use the given comparator and turns on
     * {@linkplain Autocorrect#CUSTOM_SORT custom sort mode}.
     * 
     * @param comparator
     *            the comparator to use for future suggestions
     */
    public void setComparator(Comparator<? super String> comparator) {
        this.comparator = comparator;
        sortMode = CUSTOM_SORT;
    }

    /**
     * Sets the maximum LED allowed.
     * 
     * @param maxLED
     *            the maximum LED to allow, cannot be negative
     */
    public void setMaxLED(int maxLED) {
        if (maxLED < 0) {
            throw new IllegalArgumentException(NEGATIVE_LED_MESSAGE);
        }
        this.maxLED = maxLED;
    }

    /**
     * Sets this autocorrect to use the given sort mode. Valid sort modes can be
     * passed via {@link #KEYBOARD_DISTANCE_SORT}, {@link #LED_SORT},
     * {@link #CUSTOM_SORT}, or {@link #UNSORTED} which represent integers 0-3
     * respectively. If the given sort mode is {@linkplain #CUSTOM_SORT custom
     * sort mode}, suggestions will remain unsorted until a comparator is set
     * via {@link #setComparator(Comparator)}, unless a comparator was passed
     * during construction.
     * 
     * @param sortMode
     *            the integral value of the sort mode to use
     */
    public void setSortMode(int sortMode) {
        if (sortMode < 0 || sortMode >= NUM_SORT_MODES) {
            throw new IllegalArgumentException("invalid sort mode");
        }
        this.sortMode = sortMode;
    }

    /**
     * Returns a list of all suggestions generated according to the algorithms
     * this autocorrect is using.
     * 
     * @param word
     *            the word from which to generate suggestions
     * @return a list of all suggestions, sorted by the sorting mode this
     *         autocorrect is using.
     */
    public List<String> suggest(String word) {
        if(!strictCaps){
            word = word.toLowerCase();
        }
        Set<String> suggestionSet = new HashSet<String>();
        if (contains(word)) {
            suggestionSet.add(word);
        }
        if (useLED) {
            suggestionSet.addAll(suggestLED(word));
        }
        if (useAutocomplete) {
            suggestionSet.addAll(autocomplete(word));
        }
        if (useWhitespace) {
            suggestionSet.addAll(suggestWhitespace(word));
        }
        Comparator<? super String> comp = null;
        List<String> suggestions = new ArrayList<String>(suggestionSet);
        switch (sortMode) {
        case KEYBOARD_DISTANCE_SORT:
            comp = new KeyboardDistanceComparator(word);
            break;
        case LED_SORT:
            comp = new LEDComparator(word);
            break;
        case CUSTOM_SORT:
            comp = comparator;
            break;
        }
        if (comp != null) {
            Collections.sort(suggestions, comp);
        }
        return suggestions;
    }

    /**
     * Generates a list of all words in this autocorrect's corpus within the
     * currently allowed maximum LED of the given word. The pre-defined maximum
     * LED is 3, but this can be changed using
     * {@link Autocorrect#setMaxLED(int)}.
     * 
     * @param word
     *            the word from which to generate suggestions
     * @return an unsorted list of all words in this autocorrect's corpus within
     *         the default maximum LED of the given word
     */
    public List<String> suggestLED(String word) {
        return suggestLED(word, maxLED);
    }

    /**
     * Generates a list of all words in this autocorrect's corpus within the
     * given LED of the given word. Supplying the maximum LED will temporarily
     * override, but not change, this autocorrect's current maximum allowed LED.
     * 
     * @param word
     *            word the word from which to generate suggestions
     * @param maxLED
     *            the maximum Levenshtein Edit Distance (LED) allowed, cannot be
     *            negative
     * @return an unsorted list of all words in this autocorrect's corpus within
     *         the given LED of the given word
     */
    public List<String> suggestLED(String word, int maxLED) {
        if (maxLED < 0) {
            throw new IllegalArgumentException(NEGATIVE_LED_MESSAGE);
        }
        List<String> suggestions = new ArrayList<>();
        if (maxLED == 0) {
            if (contains(word)) {
                suggestions.add(word);
            }
            return suggestions;
        }
        char[] letters = (" " + word).toCharArray();
        int[][] matrix = new int[letters.length + maxLED][letters.length];
        IntStream.range(1, letters.length).forEach(j -> matrix[0][j] = j);
        StringBuilder compWord = new StringBuilder(" ");
        root.entrySet().stream().forEach(e -> {
            compWord.append(e.getKey());
            suggestLEDAux(e.getValue(), letters, maxLED, compWord, matrix, suggestions);
            Strings.pop(compWord);
        });
        return suggestions;
    }

    /**
     * Generates a list of all valid bigrams that can be created by inserting a
     * space into the given word at any position.
     *
     * @param word
     *            the word from which to generate suggestions
     * @return a list of all valid bigrams that result from splitting the given
     *         word
     */
    public List<String> suggestWhitespace(String word) {
        List<String> suggestions = new ArrayList<String>();
        for (int i = 0; i < word.length(); i++) {
            String first = word.substring(0, i);
            String second = word.substring(i);
            if (contains(first) && contains(second)) {
                suggestions.add(first + " " + second);
            }
        }
        return suggestions;
    }
}
