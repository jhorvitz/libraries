package edu.drexel.cs.jah473.datastructures;

import java.util.List;

/**
 * A wrapper class to represent a path on a graph. The path is represented by an
 * ordered list of vertices and the total length. This class is utilized as a
 * return type for {@link AStar#solve(AStarSolvable, Object)}.
 * 
 * @author Justin Horvitz
 *
 * @param <V>
 *            the type representing a vertex of the graph
 */
public class Path<V> {
    /**
     * An ordered list of vertices on this path.
     */
    public final List<V> path;

    /**
     * The total length of this path.
     */
    public final double len;

    /* Package constructor */
    Path(List<V> vertices, double pathLength) {
        this.path = vertices;
        this.len = pathLength;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Path<?> other = (Path<?>) obj;
        if (Double.doubleToLongBits(len) != Double.doubleToLongBits(other.len))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return true;
    }

    /**
     * Returns a string representation of this path.
     */
    @Override
    public String toString() {
        return "Path [path=" + path + ", pathLength=" + len + "]";
    }

}
