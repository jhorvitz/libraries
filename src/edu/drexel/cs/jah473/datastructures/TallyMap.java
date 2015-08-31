package edu.drexel.cs.jah473.datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

import edu.drexel.cs.jah473.util.Stats;

/**
 * A map that tallies up counts for its keys.
 * 
 * @author Justin Horvitz
 *
 * @param <K>
 *            the type of keys in this tally map
 */
public class TallyMap<K> implements Serializable {

    private static final long serialVersionUID = -207017386938682316L;
    private final BiFunction<K, Integer, Integer> increment = (k, count) -> count == null ? 1 : count + 1;
    protected final Map<K, Integer> map = new HashMap<>();
    protected final List<K> maxKeys = new ArrayList<>();
    protected int maxTally = 0;

    /**
     * Returns a new tally map representing a frequency distribution for the
     * given collection.
     * 
     * @param <K> the type of keys in the collection and the resulting tally map
     * 
     * @param c
     *            the collection
     * @return a new tally map with counts reflecting the frequency of objects
     *         in the given collection
     */
    public static <K> TallyMap<K> of(Collection<K> c) {
        TallyMap<K> tm = new TallyMap<>();
        c.stream().forEach(tm::increment);
        return tm;
    }

    /**
     * Returns a list containing the key(s) with the highest tally. If there is
     * only one key with the highest tally, the list returned will contain one
     * element. If there are multiple keys tied for the highest tally, the list
     * returned will contain multiple elements. If there are no tallies in this
     * map, the list returned will be empty.
     * 
     * @return a list containing the key(s) with the highest tally
     */
    public List<K> getMaxKeys() {
        return new ArrayList<>(maxKeys);
    }

    /**
     * Returns the highest tally in this map.
     * 
     * @return the highest tally in this map
     */
    public int getMaxTally() {
        return maxTally;
    }

    /**
     * Returns the tally for the given key.
     * 
     * @param key
     *            the key whose tally is to be returned
     * @return the tally for the given key
     */
    public int getTally(K key) {
        return map.getOrDefault(key, 0);
    }

    /**
     * Increases the given key's tally by one.
     * 
     * @param key
     *            the key whose tally is to be incremented
     * @return the new tally associated with the key
     */
    public int increment(K key) {
        int updated = map.compute(key, increment);
        if (updated >= maxTally) {
            if (updated > maxTally) {
                maxKeys.clear();
                maxTally = updated;
            }
            maxKeys.add(key);
        }
        return updated;
    }

    /**
     * Retrieves the keys with the top k highest tallies.
     * 
     * @param k
     *            the number of keys to return
     * @return the keys with the top k highest tallies
     */
    public List<K> getMaxKeys(int k) {
        if (k <= 0) {
            return new ArrayList<>();
        }
        if (k <= maxKeys.size()) {
            return getMaxKeys();
        }
        return Stats.kLargest(map.keySet(), k, (k1, k2) -> Integer.compare(map.get(k1), map.get(k2)));
    }
    
    /**
     * Returns the number of unique keys in this tally map.
     * @return The number of unique keys in this tally map
     */
    public int size(){
        return map.size();
    }

    /**
     * Returns the entry set backing this tally map.
     * 
     * @return the entry set backing this tally map
     */
    public Set<Entry<K, Integer>> entrySet() {
        return map.entrySet();
    }
}
