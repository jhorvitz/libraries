package edu.drexel.cs.jah473.util;

import java.io.Serializable;

/**
 * Represents a 2-tuple.
 * 
 * @author Justin Horvitz
 *
 * @param <X>
 *            the type of the first element of each pair
 * @param <Y>
 *            the type of the second element of each pair
 */
public class Pair<X, Y> implements Serializable {

    private static final long serialVersionUID = -453332252479175824L;
    /**
     * The first element in the pair.
     */
    public X fst;
    /**
     * The second element in the pair.
     */
    public Y snd;

    /**
     * Constructs a new pair from the given values.
     * 
     * @param fst
     *            the value of the first element
     * @param snd
     *            the value of the second element
     */
    public Pair(X fst, Y snd) {
        this.fst = fst;
        this.snd = snd;
    }

    /**
     * Returns a string representation of this pair.
     */
    @Override
    public String toString() {
        return "(" + fst + "," + snd + ")";
    }

    /**
     * Compares the specified object with this pair for equality.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        if (fst == null) {
            if (other.fst != null)
                return false;
        } else if (!fst.equals(other.fst))
            return false;
        if (snd == null) {
            if (other.snd != null)
                return false;
        } else if (!snd.equals(other.snd))
            return false;
        return true;
    }

    /**
     * Returns the hash code value for this pair.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fst == null) ? 0 : fst.hashCode());
        result = prime * result + ((snd == null) ? 0 : snd.hashCode());
        return result;
    }
}
