package edu.drexel.cs.jah473.datastructures;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Represents a dynamic trie data structure for maintaining a set of strings.
 * Supports insertion, deletion, and lookup in O(m) time where m is the length
 * of the string in question. This implementation can be expected to perform as
 * well as or slightly better than {@link java.util.TreeSet TreeSet} for large
 * collections of strings and will also maintain a natural ordering as long as
 * all strings in the collection are composed only of letters of the English
 * alphabet.<br>
 * <br>
 * 
 * This class implements both the {@link Collection} and {@link Set} interfaces
 * for strings.
 * 
 * @author Justin Horvitz
 *
 */
public class Trie implements Set<String>, Collection<String>, Serializable {

    /* Pre-order iterator */
    protected static final class TrieIterator implements Iterator<String> {

        private String next;
        private Deque<Iterator<Entry<Character, TrieNode>>> q = new ArrayDeque<>();
        private StringBuilder sb = new StringBuilder();

        public TrieIterator(TrieNode node, String prefix) {
            sb.append(prefix);
            q.push(node.entrySet().iterator());
            if (node.isWord) {
                next = prefix;
            } else {
                findNext();
            }
        }

        private void findNext() {
            next = null;
            Iterator<Entry<Character, TrieNode>> iterator = q.peek();
            while (iterator != null) {
                while (iterator.hasNext()) {
                    Entry<Character, TrieNode> e = iterator.next();
                    char key = e.getKey();
                    sb.append(key);
                    TrieNode node = e.getValue();
                    iterator = node.entrySet().iterator();
                    q.push(iterator);
                    if (node.isWord) {
                        next = sb.toString();
                        return;
                    }
                }
                q.pop();
                int len = sb.length();
                if (len > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                iterator = q.peek();
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public String next() {
            String ret = next;
            findNext();
            return ret;
        }

    }

    /* Node representation */
    protected static class TrieNode extends HashMap<Character, TrieNode> implements Serializable {
        private static final long serialVersionUID = -7722726589935504620L;
        protected boolean isWord = false;
        protected TrieNode parent;

        protected TrieNode() {
            super(ENGLISH_ALPHABET_SIZE, 1F);
        }

        /* Alternate constructor for root node in strict caps mode */
        protected TrieNode(boolean b) {
            super(ENGLISH_ALPHABET_SIZE * 2, 1F);
        }

        public boolean isWord() {
            return isWord;
        }

        @Override
        public void clear() {
            super.clear();
            isWord = false;
            parent = null;
        }

    }

    protected static final int ENGLISH_ALPHABET_SIZE = 26;

    private static final long serialVersionUID = -5788517086724562891L;
    protected TrieNode root;

    protected int size = 0;
    /**
     * Whether this trie considers a word with capital letter(s) to be different
     * from its lower case version. This setting can only be changed during
     * construction.
     */
    public final boolean strictCaps;

    /**
     * Constructs a new trie. The returned instance will not use strict caps
     * mode.
     */
    public Trie() {
        this(false);
    }

    /**
     * Constructs a new trie with the indicated strict caps mode.
     * 
     * @param strictCaps
     *            whether to treat capital letters as unique
     */
    public Trie(boolean strictCaps) {
        this.strictCaps = strictCaps;
        if (strictCaps) {
            root = new TrieNode(false);
        } else {
            root = newNode();
        }
    }

    /**
     * Constructs a new trie containing all the words in the given collection.
     * The returned instance will not use strict caps mode.
     * 
     * @param words the collection of strings to be contained in the trie
     */
    public Trie(Collection<String> words) {
        this(words, false);
    }

    /**
     * Constructs a new trie containing all strings in the given collection and
     * using the given strict caps mode.
     * 
     * @param words
     *            the collection of strings to be contained in the trie
     * @param strictCaps
     *            whether to treat capital letters as unique
     */
    public Trie(Collection<String> words, boolean strictCaps) {
        this(strictCaps);
        for (String word : words) {
            if (!strictCaps) {
                word = word.toLowerCase();
            }
            add(word);
        }
    }

    /**
     * Adds a word to this trie.
     * 
     * @param word
     *            the word to add
     * @return {@code true} if the word was not already present in this trie
     */
    @Override
    public boolean add(String word) {
        if (!strictCaps) {
            word = word.toLowerCase();
        }
        TrieNode node = root;
        int len = word.length();
        for (int i = 0; i < len; i++) {
            Character c = word.charAt(i);
            TrieNode next = node.get(c);
            if (next == null) {
                next = newNode();
                node.put(c, next);
                next.parent = node;
            }
            node = next;
        }
        if (node.isWord) {
            return false;
        } else {
            node.isWord = true;
            size++;
            return true;
        }
    }

    /**
     * Adds all of the strings in the collection to this trie.
     * 
     * @param words
     *            the strings to add
     * @return {@code true} if this trie changed as a result of the call
     */
    @Override
    public boolean addAll(Collection<? extends String> words) {
        boolean changed = false;
        for (String word : words) {
            if (add(word))
                changed = true;
        }
        return changed;
    }

    /**
     * Clears all data from this trie. This trie will be a single empty root
     * node after this call returns.
     */
    @Override
    public void clear() {
        root.clear();
        size = 0;
    }

    /**
     * Returns true if this trie contains the specified element.
     * 
     * @param o
     *            the element in question
     * @return {@code true} if this trie contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        if (!(o instanceof String)) {
            return false;
        }
        TrieNode node = findNode((String) o);
        return node != null && node.isWord;
    }

    /**
     * Returns true if this trie contains all of the elements in the specified
     * collection.
     * 
     * @param c
     *            the collection in question
     * @return {@code true} if this tree contains all of the elements in the
     *         collection
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    /* Internal function to find the node for a string */
    protected TrieNode findNode(String word) {
        if (!strictCaps) {
            word = word.toLowerCase();
        }
        TrieNode node = root;
        int len = word.length();
        for (int i = 0; i < len; i++) {
            Character c = word.charAt(i);
            node = node.get(c);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /**
     * Returns true if this tree contains no elements.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Determines whether the given string is a prefix of any words in this
     * trie.
     * 
     * @param str
     *            the string to test
     * @return {@code true} if the given string is a prefix of any words in this
     *         trie
     */
    public boolean isPrefix(String str) {
        return findNode(str) != null;
    }

    /**
     * Returns an iterator for this trie. The iterator is guaranteed to traverse
     * the strings in standard string order as long as all strings inserted into
     * this trie consist only of letters of the English alphabet.
     */
    @Override
    public Iterator<String> iterator() {
        return new TrieIterator(root, "");
    }

    /* Allocates memory for a new node */
    protected TrieNode newNode() {
        return new TrieNode();
    }

    /**
     * Removes the specified element from this trie, if it is present.
     * 
     * @param o
     *            the element to remove
     * @return {@code true} if this trie was changes as a result of this call
     */
    @Override
    public boolean remove(Object o) {
        if (!(o instanceof String)) {
            return false;
        }
        String word = (String) o;
        TrieNode node = findNode(word);
        if (node != null && node.isWord) {
            node.isWord = false;
            size--;
            if (node.isEmpty()) {
                int depth = word.length();
                do {
                    node = node.parent;
                    depth--;
                } while (depth > 0 && !node.isWord && node.size() == 1);
                node.remove(word.charAt(depth));
            }
            return true;
        }
        return false;
    }

    /**
     * Removes all of the elements in the specified collection from this trie.
     * 
     * @param c
     *            the collection of elements to remove
     * @return {@code true} if this trie was changed as a result of this call
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            if (remove(o)) {
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Retains only the elements in this trie that are contained in the
     * specified collection.
     * 
     * @param c
     *            the collection of elements to retain
     * @return {@code true} if this trie was changed as a result of this call
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        Iterator<String> iterator = iterator();
        List<String> toRemove = new ArrayList<>(size - c.size());
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (!c.contains(s)) {
                toRemove.add(s);
            }
        }
        return removeAll(toRemove);
    }

    /**
     * Returns the number of valid words in this trie.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns an array containing all of the strings in this trie.
     * 
     * @return an array containing all of the elements in this trie
     */
    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        Iterator<String> iterator = iterator();
        int i = 0;
        while (iterator.hasNext()) {
            array[i++] = iterator.next();
        }
        return array;
    }

    /**
     * Returns an array containing all of the valid words in this trie. The
     * runtime type of the returned array is that of the specified array. If all
     * of the elements in this trie fit in the specified array, it is returned
     * therein. Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this trie.
     * 
     * @param a
     *            the array into which the elements of the list are to be
     *            stored, if it is big enough; otherwise, a new array of the
     *            same runtime type is allocated for this purpose
     * @return an array containing the valid words in this trie
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (size > a.length) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        }
        Iterator<String> iterator = iterator();
        int i = 0;
        while (iterator.hasNext()) {
            a[i++] = (T) iterator.next();
        }
        return a;
    }

}
