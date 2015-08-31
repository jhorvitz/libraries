package edu.drexel.cs.jah473.datastructures;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.drexel.cs.jah473.datastructures.Trie;
import edu.drexel.cs.jah473.datastructures.Trie.TrieNode;

/*
 * JUnit Tests for Trie class. All 11 tests passed in 11.2 seconds.
 * Code coverage of Trie.java was 100%.
 */

public class TrieTest {

    static final int MAX_WORD_LENGTH = 15;
    static final int NUM_TRIALS = 100;
    static final int NUM_WORDS = 100_000;
    static Set<String> prefixes = new HashSet<>(NUM_WORDS * MAX_WORD_LENGTH / 2);
    static final Random RAND = new Random();
    static Trie trie = new Trie();
    static Set<String> words = new HashSet<>(NUM_WORDS);

    static char randChar() {
        return (char) (RAND.nextInt(26) + 'a');
    }

    static String randWord(int maxLen) {
        StringBuilder sb = new StringBuilder();
        int len = RAND.nextInt(maxLen) + 1;
        for (int i = 0; i < len; i++) {
            sb.append(randChar());
        }
        return sb.toString();
    }

    static String randWordPlus() {
        StringBuilder sb = new StringBuilder();
        int len = RAND.nextInt(MAX_WORD_LENGTH) + 1;
        for (int i = 0; i < len; i++) {
            sb.append(randChar());
            prefixes.add(sb.toString());
        }
        return sb.toString();
    }

    @BeforeClass
    public static void setup() {
        for (int i = 0; i < NUM_WORDS; i++) {
            String word = randWordPlus();
            words.add(word);
            trie.add(word);
        }
        prefixes.add("");
        assertEquals(words.size(), trie.size());
    }

    @Test
    public void addAllTest() {
        List<String> listOfWords = words.stream().limit(NUM_TRIALS).collect(Collectors.toList());
        final int origSize = trie.size();
        assertFalse(trie.addAll(listOfWords));
        assertEquals(origSize, trie.size());
        String newWord;
        do {
            newWord = randWordPlus();
        } while (words.contains(newWord));
        assertFalse(trie.contains(newWord));
        listOfWords.add(newWord);
        boolean naiveSays = words.addAll(listOfWords);
        boolean trieSays = trie.addAll(listOfWords);
        assertEquals(naiveSays, trieSays);
        assertEquals(words.size(), trie.size());
    }

    @Test
    public void addTest() {
        for (int i = 0; i < NUM_TRIALS; i++) {
            String word = randWordPlus();
            boolean naiveSays = words.add(word);
            boolean trieSays = trie.add(word);
            assertEquals(naiveSays, trieSays);
            assertEquals(words.size(), trie.size());
            assertTrue(trie.contains(word));
        }
    }

    @Test
    public void altConstructorAndClearTest() {
        Trie trie2 = new Trie(words, false);
        Object[] trieArr = trie.toArray();
        Object[] trie2Arr = trie2.toArray();
        Arrays.sort(trieArr);
        Arrays.sort(trie2Arr);
        assertArrayEquals(trieArr, trie2Arr);
        trie2.clear();
        assertTrue(trie2.isEmpty());
        assertFalse(trie.isEmpty());
        assertArrayEquals(new Object[0], trie2.toArray());
    }

    @Test
    public void containsAllTest() {
        List<String> listOfWords = words.stream().limit(NUM_TRIALS).collect(Collectors.toList());
        assertTrue(trie.containsAll(listOfWords));
        String newWord;
        do {
            newWord = randWordPlus();
        } while (words.contains(newWord));
        listOfWords.add(newWord);
        assertFalse(trie.containsAll(listOfWords));
    }

    @Test
    public void containsTest() {
        for (int i = 0; i < NUM_TRIALS; i++) {
            String word = randWord(MAX_WORD_LENGTH);
            boolean naiveSays = words.contains(word);
            boolean trieSays = trie.contains(word);
            assertEquals(naiveSays, trieSays);
        }
        Integer notString = 5;
        assertFalse(trie.contains(notString));
    }

    @Test
    public void isPrefixTest() {
        for (int i = 0; i < NUM_TRIALS; i++) {
            String word = randWord(MAX_WORD_LENGTH);
            boolean naiveSays = prefixes.contains(word);
            boolean trieSays = trie.isPrefix(word);
            assertEquals(naiveSays, trieSays);
        }
    }

    @Test
    public void iteratorTest() throws Exception {
        TrieNode node = trie.root;
        StringBuilder sb = new StringBuilder();
        do {
            Entry<Character, TrieNode> e = node.entrySet().stream().findFirst()
                    .orElseThrow(() -> new Exception("empty TrieNode"));
            sb.append(e.getKey());
            node = e.getValue();
        } while (!node.isWord);
        String prefix = sb.toString();
        Iterator<String> iterator = new Trie.TrieIterator(node, prefix);
        List<String> wordsBelow = new ArrayList<>();
        while (iterator.hasNext()) {
            wordsBelow.add(iterator.next());
        }
        wordsBelow.stream().forEach(w -> assertTrue(w.startsWith(prefix)));
    }

    @Test
    public void removeAllTest() {
        List<String> listOfWords = new ArrayList<>(NUM_TRIALS);
        for (int i = 0; i < NUM_TRIALS; i++) {
            listOfWords.add(randWordPlus());
        }
        boolean naiveSays = words.removeAll(listOfWords);
        boolean trieSays = trie.removeAll(listOfWords);
        assertEquals(naiveSays, trieSays);
        assertEquals(words.size(), trie.size());
        words.addAll(listOfWords);
        trie.addAll(listOfWords);
        listOfWords.removeIf(w -> words.contains(w));
        assertFalse(trie.removeAll(listOfWords));
        assertEquals(words.size(), trie.size());
    }

    @Test
    public void removeTest() {
        for (int i = 0; i < NUM_TRIALS / 2; i++) {
            String word = randWord(MAX_WORD_LENGTH);
            boolean naiveSays = words.remove(word);
            boolean trieSays = trie.remove(word);
            assertEquals(naiveSays, trieSays);
            assertEquals(words.size(), trie.size());
            assertFalse(trie.contains(word));
            if (trieSays) {
                words.add(word);
                trie.add(word);
            }
        }
        Object[] wordsArr = words.toArray();
        for (int i = 0; i < NUM_TRIALS / 2; i++) {
            Object word = wordsArr[RAND.nextInt(wordsArr.length)];
            boolean naiveSays = words.remove(word);
            boolean trieSays = trie.remove(word);
            assertEquals(naiveSays, trieSays);
            assertEquals(words.size(), trie.size());
            assertFalse(trie.contains(word));
            if (trieSays) {
                words.add((String) word);
                trie.add((String) word);
            }
        }
        Integer notString = 5;
        assertFalse(trie.remove(notString));
    }

    @Test
    public void retainAllTest() {
        assertFalse(trie.retainAll(words));
        assertEquals(trie.size(), words.size());
        Set<String> toRetain = words.stream().limit(words.size() - NUM_TRIALS).collect(Collectors.toSet());
        Set<String> removed = words.stream().filter(w -> !toRetain.contains(w)).collect(Collectors.toSet());
        boolean naiveSays = words.retainAll(toRetain);
        boolean trieSays = trie.retainAll(toRetain);
        assertEquals(naiveSays, trieSays);
        assertEquals(words.size(), trie.size());
        words.addAll(removed);
        trie.addAll(removed);
    }

    @Test
    public void serializeTest() throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("out.dat"));
        oos.writeObject(trie);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("out.dat"));
        Trie old = trie;
        trie = (Trie) ois.readObject();
        ois.close();
        assertEquals(old.size(), trie.size());
        assertEquals(old.strictCaps, trie.strictCaps);
    }

    @Test
    public void strictCapsTest() {
        Trie normal = new Trie(false);
        Trie strict = new Trie(true);
        String myName = "Justin";
        normal.add(myName);
        strict.add(myName);
        String myNameLower = myName.toLowerCase();
        assertTrue(normal.contains(myName));
        assertTrue(normal.contains(myNameLower));
        assertTrue(strict.contains(myName));
        assertFalse(strict.contains(myNameLower));
    }

    @Test
    public void toArrayTest() {
        Object[] naiveArr = words.toArray();
        Object[] trieArr = trie.toArray();
        String[] strArr = trie.toArray(new String[0]);
        String[] strArr2 = trie.toArray(new String[trie.size()]);
        Arrays.sort(naiveArr);
        Arrays.sort(trieArr);
        Arrays.sort(strArr);
        Arrays.sort(strArr2);
        assertArrayEquals(naiveArr, trieArr);
        assertArrayEquals(naiveArr, strArr);
        assertArrayEquals(naiveArr, strArr2);
    }
}
