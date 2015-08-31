package edu.drexel.cs.jah473.datastructures;

import java.util.List;

/**
 * A graph containing certain methods to make it solvable via the A* search
 * algorithm.
 * 
 * @author Justin Horvitz
 *
 * @param <V>
 *            the type representing a vertex on the graph
 */
public interface AStarSolvable<V> {
    /**
     * Returns a list of vertices adjacent to the given vertex.
     * 
     * @param vertex
     *            the given vertex
     * @return a list of adjacent vertices
     */
    List<V> getAdjacentTo(V vertex);

    /**
     * Calculates the weight (cost) of going from vertex {@code from} to vertex
     * {@code to}.
     * 
     * @param from
     *            the current vertex
     * @param to
     *            the potential next vertex
     * @return the weight between {@code from} and {@code to}
     */
    double weight(V from, V to);

    /**
     * Determines whether the given vertex represents a goal state
     * (destination).
     * 
     * @param vertex
     *            the vertex in question
     * @return {@code true} if the given vertex represents a goal state
     */
    boolean isGoal(V vertex);

    /**
     * Estimates the minimum cost from the given vertex to the goal state. The
     * heuristic cannot be an overestimate, or else the search may not return
     * the minimum-cost path.
     * 
     * @param vertex
     *            the vertex from which to estimate distance to the goal
     * @return the estimated minimum cost from {@code vertex} to the goal state
     */
    double heuristic(V vertex);

    /**
     * Returns the initial capacity to use for the A* priority queue. The
     * default implementation returns {@code 11}, which is the default initial
     * capacity of {@link java.util.PriorityQueue}.
     * 
     * @return the initial capacity to use for the A* priority queue
     */
    default int capacity() {
        return 11;
    }
}
