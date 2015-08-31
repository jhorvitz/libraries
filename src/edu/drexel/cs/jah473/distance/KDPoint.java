package edu.drexel.cs.jah473.distance;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Class to represent a point in k-dimensional space. Compatible with
 * {@link edu.drexel.cs.jah473.datastructures.KDTree KDTree} data structure. The
 * coordinates cannot be modified after construction. This class is intended to
 * be extended to incorporate auxiliary information about a data point in
 * addition to just the coordinates.
 * 
 * @author Justin Horvitz
 *
 */
public class KDPoint implements Serializable {

    private static final long serialVersionUID = 6170725459217416343L;
    protected final double[] coords;

    /**
     * Constructs a new KD Point.
     * 
     * @param coords
     *            the k coordinates of the point
     */
    public KDPoint(double... coords) {
        int k = coords.length;
        if (k < 1) {
            throw new IllegalArgumentException("k must be greater than or equal to 1");
        }
        this.coords = Arrays.copyOf(coords, k);
    }

    /**
     * Compares the specified object with this point for equality.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KDPoint other = (KDPoint) obj;
        if (!Arrays.equals(coords, other.coords))
            return false;
        return true;
    }

    /**
     * Gets the coordinate for a particular dimension.
     * 
     * @param dimension
     *            the numerical representation of the dimension desired
     * @return this point's coordinate for the dimension in question
     */
    public final double getCoord(int dimension) {
        return coords[dimension];
    }

    /**
     * Gets all k coordinates of this point.
     * 
     * @return an array of coordinates
     */
    public final double[] getCoords() {
        return Arrays.copyOf(coords, coords.length);
    }

    /**
     * Gets the number of dimensions.
     * 
     * @return the number of dimensions
     */
    public final int getK() {
        return coords.length;
    }

    /**
     * Returns the hash code value for this point.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(coords);
        return result;
    }

    /**
     * Returns the string representation of this point.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (double coord : coords) {
            sb.append(coord);
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(')');
        return sb.toString();
    }
}
