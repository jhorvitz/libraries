package edu.drexel.cs.jah473.datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Contains methods to find the shortest path via the A* search algorithm.
 * 
 * @author Justin Horvitz
 *
 */
public final class AStar {

    /* Private constructor to prevent instantiation. */
    private AStar() {

    }

    /**
     * Finds the shortest path from the specified source vertex to the graph's
     * goal state.
     * 
     * @param <V> the vertex type of the given graph
     * 
     * @param graph
     *            the graph
     * @param source
     *            the source vertex
     * @return the shortest path from the source vertex to a goal state as
     *         defined by the graph's {@link AStarSolvable#isGoal(Object)}
     *         method, or null if no path exists
     */
    public static <V> Path<V> solve(AStarSolvable<V> graph, V source) {
        Set<V> seen = new HashSet<>(graph.capacity());
        Queue<Wrapper<V>> pq = new PriorityQueue<>(graph.capacity());
        pq.add(new Wrapper<V>(source, null, 0, graph.heuristic(source)));
        while (!pq.isEmpty()) {
            Wrapper<V> w = pq.remove();
            V curr = w.vertex;
            if (seen.contains(curr)) {
                continue;
            }
            seen.add(curr);
            double cost = w.cost;
            if (graph.isGoal(curr)) {
                return new Path<>(backtrack(w), cost);
            }
            List<V> neighbors = graph.getAdjacentTo(curr);
            for (V next : neighbors) {
                double weight = graph.weight(curr, next);
                double heuristic = graph.heuristic(next);
                pq.add(new Wrapper<>(next, w, cost + weight, heuristic));
            }
        }
        return null;
    }

    private static <V> List<V> backtrack(Wrapper<V> goal) {
        List<V> path = new ArrayList<>();
        Wrapper<V> curr = goal;
        while (curr != null) {
            path.add(curr.vertex);
            curr = curr.prev;
        }
        Collections.reverse(path);
        return path;
    }

    /* Wraps a node with auxiliary info */
    private static class Wrapper<V> implements Comparable<Wrapper<V>> {
        V vertex;
        Wrapper<V> prev;
        double cost;
        double heuristic;

        Wrapper(V current, Wrapper<V> prev, double cost, double heuristic) {
            this.vertex = current;
            this.prev = prev;
            this.cost = cost;
            this.heuristic = heuristic;
        }

        @Override
        public int compareTo(Wrapper<V> o) {
            return Double.compare(cost + heuristic, o.cost + o.heuristic);
        }
    }
}
