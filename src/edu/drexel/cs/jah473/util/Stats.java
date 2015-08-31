package edu.drexel.cs.jah473.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

import edu.drexel.cs.jah473.datastructures.TallyMap;

public final class Stats {

    /**
     * Finds the k largest elements in the given collection according to the
     * elements' natural ordering. The list returned will be sorted in
     * nonincreasing order. If k is larger than the size of the given
     * collection, the list returned will be of size k. If k is less than or
     * equal to 0, the list returned will be empty.
     * 
     * @param <T>
     *            the component type of the given collection
     * 
     * @param ts
     *            the collection of elements
     * @param k
     *            how many of the largest elements to find
     * @return a list of the k largest elements in the collection, sorted in
     *         nonincreasing order
     */
    public static <T extends Comparable<? super T>> List<T> kLargest(Collection<T> ts, int k) {
        return kLargest(ts, k, (t1, t2) -> t1.compareTo(t2));
    }

    /**
     * Finds the k largest elements in the given collection according to the
     * given comparator. The list returned will be sorted in nonincreasing
     * order. If k is larger than the size of the given collection, the list
     * returned will be of size k. If k is less than or equal to 0, the list
     * returned will be empty.
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection of elements
     * @param k
     *            how many of the largest elements to find
     * @param comparator
     *            the comparator to use for comparing elements
     * @return a list of the k largest elements in the collection, sorted in
     *         nonincreasing order
     */
    public static <T> List<T> kLargest(Collection<T> ts, int k, Comparator<? super T> comparator) {
        if (k >= ts.size()) {
            List<T> list = new ArrayList<>(ts);
            Collections.sort(list, (t1, t2) -> comparator.compare(t2, t1));
            return list;
        }
        if (k <= 0) {
            return new ArrayList<>();
        }
        Queue<T> pq = new PriorityQueue<>(k, comparator);
        Iterator<T> iterator = ts.iterator();
        for (int i = 0; i < k; i++) {
            pq.add(iterator.next());
        }
        T min = pq.peek();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (comparator.compare(t, min) > 0) {
                pq.remove();
                pq.add(t);
                min = pq.peek();
            }
        }
        List<T> list = new ArrayList<>(k);
        while (!pq.isEmpty()) {
            list.add(pq.remove());
        }
        return list;
    }

    /**
     * Finds the k smallest elements in the given collection according to the
     * elements' natural ordering. The list returned will be sorted in
     * nondecreasing order. If k is larger than the size of the given
     * collection, the list returned will be of size k. If k is less than or
     * equal to 0, the list returned will be empty.
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection of elements
     * @param k
     *            how many of the smallest elements to find
     * @return a list of the k smallest elements in the collection, sorted in
     *         nondecreasing order
     */
    public static <T extends Comparable<? super T>> List<T> kSmallest(Collection<T> ts, int k) {
        return kLargest(ts, k, (t1, t2) -> t2.compareTo(t1));
    }

    /**
     * Finds the k smallest elements in the given collection according to the
     * given comparator. The list returned will be sorted in nondecreasing
     * order. If k is larger than the size of the given collection, the list
     * returned will be of size k. If k is less than or equal to 0, the list
     * returned will be empty.
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection of elements
     * @param k
     *            how many of the smallest elements to find
     * @param comparator
     *            the comparator to use for comparing elements
     * @return a list of the k smallest elements in the collection, sorted in
     *         nondecreasing order
     */
    public static <T> List<T> kSmallest(Collection<T> ts, int k, Comparator<? super T> comparator) {
        return kLargest(ts, k, (t1, t2) -> comparator.compare(t2, t1));
    }

    /**
     * Finds the maximum element of the given collection, according to the
     * elements' natural ordering
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection
     * @return the maximum element, or {@code null} if the collection is empty
     */
    public static <T extends Comparable<? super T>> T max(Collection<T> ts) {
        return max(ts, (t1, t2) -> t1.compareTo(t2));
    }

    /**
     * Finds the maximum element in the given collection, according to the
     * specified comparator.
     * 
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection
     * @param comparator
     *            the comparator
     * @return the maximum element, or {@code null} if the collection is empty
     */
    public static <T> T max(Collection<T> ts, Comparator<? super T> comparator) {
        return min(ts, (t1, t2) -> comparator.compare(t2, t1));
    }

    /**
     * Finds the maximum element among the given parameters, according to their
     * natural ordering.
     * 
     * @param <T>
     *            the type of the given arguments
     * @param ts
     *            the elements among which the maximum is to be found
     * @return the maximum element, or {@code null} if no parameters are given
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> T max(T... ts) {
        return max(Arrays.asList(ts));
    }

    /**
     * Calculates the mean of the given collection.
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection
     * @return the mean of the collection, or {@link Double#isNaN()} if the
     *         collection is empty
     */
    public static <T extends Number> double mean(Collection<T> ts) {
        return ts.stream().mapToDouble(Number::doubleValue).average().orElse(Double.NaN);
    }

    /**
     * Calculates the mean of the given parameters.
     * 
     * @param <T>
     *            the type of the given arguments
     * @param ts
     *            the parameters from which to calculate the mean
     * @return the mean of the parameters, or {@link Double#isNaN()} if no
     *         parameters are given
     */
    @SafeVarargs
    public static <T extends Number> double mean(T... ts) {
        return Arrays.stream(ts).mapToDouble(Number::doubleValue).average().orElse(Double.NaN);
    }

    /**
     * Finds the maximum element of the given collection, according to the
     * elements' natural ordering
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection
     * @return the maximum element, or {@code null} if the collection is empty
     */
    public static <T extends Comparable<? super T>> T min(Collection<T> ts) {
        return min(ts, (t1, t2) -> t1.compareTo(t2));
    }

    /**
     * Finds the minimum element in the given collection, according to the
     * specified comparator.
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection
     * @param comparator
     *            the comparator
     * @return the minimum element, or {@code null} if the collection is empty
     */
    public static <T> T min(Collection<T> ts, Comparator<? super T> comparator) {
        if (ts.size() == 0) {
            return null;
        }
        Iterator<T> iterator = ts.iterator();
        T min = iterator.next();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (comparator.compare(t, min) < 0) {
                min = t;
            }
        }
        return min;
    }

    /**
     * Finds the minimum element among the given parameters, according to their
     * natural ordering.
     * 
     * @param <T>
     *            the type of the given arguments
     * @param ts
     *            the elements among which the minimum is to be found
     * @return the minimum element, or {@code null} if no parameters are given
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> T min(T... ts) {
        return min(Arrays.asList(ts));
    }

    /**
     * Calculates the mode(s) of the given collection. The list returned may
     * contain multiple elements if there are multiple elements occurring with
     * the greatest frequency.
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection
     * @return the mode(s) of the given collection
     */
    public static <T> List<T> mode(Collection<T> ts) {
        TallyMap<T> tally = new TallyMap<>();
        for (T t : ts) {
            tally.increment(t);
        }
        return tally.getMaxKeys();
    }

    /* Sum of squares */
    private static <T extends Number> double ss(Collection<T> ts) {
        double mean = mean(ts);
        return ts.stream().mapToDouble(t -> Math.pow(mean - t.doubleValue(), 2)).sum();
    }

    /**
     * Calculates the standard deviation of the given numerical collection.
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection
     * @return the standard deviation of the collection
     */
    public static <T extends Number> double stdev(Collection<T> ts) {
        return Math.sqrt(variance(ts));
    }

    /**
     * Calculates the population standard deviation of the given numerical
     * collection.
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection
     * @return the population standard deviation of the collection
     */
    public static <T extends Number> double stdevP(Collection<T> ts) {
        return Math.sqrt(varianceP(ts));
    }

    /**
     * Calculates the variance of the given numerical collection.
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection
     * @return the variance of the collection
     */
    public static <T extends Number> double variance(Collection<T> ts) {
        return ss(ts) / (ts.size() - 1);
    }

    /**
     * Calculates the population variance of the given numerical collection.
     * @param <T> the component type of the given collection
     * @param ts
     *            the collection
     * @return the population variance of the collection
     */
    public static <T extends Number> double varianceP(Collection<T> ts) {
        return ss(ts) / ts.size();
    }

    /**
     * Calculates z-scores for each element in the given collection. The array
     * returned is parallel with the collection. That is, z-scores in the array
     * correspond to elements in the collection in the order in which they are
     * visited by the collection's iterator.
     * 
     * @param <T>
     *            the component type of the given collection
     * @param ts
     *            the collection
     * @return an array of z-scores, parallel to the collection
     */
    public static <T extends Number> double[] zScores(Collection<T> ts) {
        double mean = mean(ts);
        double sd = stdevP(ts);
        return ts.stream().mapToDouble(t -> (t.doubleValue() - mean) / sd).toArray();
    }
}
