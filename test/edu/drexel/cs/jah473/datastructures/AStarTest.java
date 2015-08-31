package edu.drexel.cs.jah473.datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AStarTest {
    static final Random RAND = new Random();
    static final int NUM_TRIALS = 100;
    static final int N = 100;
    static double[][] adjMatrix = new double[N][N];

    static class GraphSolve implements AStarSolvable<Integer> {
        int dest;

        GraphSolve(int dest) {
            this.dest = dest;
        }

        @Override
        public List<Integer> getAdjacentTo(Integer vertex) {
            List<Integer> adj = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                if (i != vertex && adjMatrix[vertex][i] < Double.POSITIVE_INFINITY) {
                    adj.add(i);
                }
            }
            return adj;
        }

        @Override
        public double weight(Integer from, Integer to) {
            return adjMatrix[from][to];
        }

        @Override
        public boolean isGoal(Integer vertex) {
            return vertex == dest;
        }

        @Override
        public double heuristic(Integer vertex) {
            return 0;
        }

    };

    private static Path<Integer> floyd(int source, int dest) {
        double[][] W = new double[N][];
        Integer[][] P = new Integer[N][N];
        for (int i = 0; i < N; i++) {
            W[i] = Arrays.copyOf(adjMatrix[i], N);
            for (int j = 0; j < N; j++) {
                if (i == j) {
                    P[i][j] = null;
                } else {
                    P[i][j] = adjMatrix[i][j] < Double.POSITIVE_INFINITY ? i : null;
                }
            }
        }
        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    double alt = W[i][k] + W[k][j];
                    if (alt < W[i][j]) {
                        W[i][j] = alt;
                        P[i][j] = P[k][j];
                    }
                }
            }
        }
        double cost = W[source][dest];
        if (cost == Double.POSITIVE_INFINITY) {
            return null;
        }
        List<Integer> path = new ArrayList<>();
        while (dest != source) {
            path.add(dest);
            dest = P[source][dest];
        }
        path.add(source);
        Collections.reverse(path);
        return new Path<Integer>(path, cost);
    }

    @Before
    public void setGraph() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i == j) {
                    adjMatrix[i][j] = 0;
                } else {
                    adjMatrix[i][j] = RAND.nextBoolean() ? RAND.nextDouble() : Double.POSITIVE_INFINITY;
                }
            }
        }
    }

    @Test
    public void solveTest() {
        for (int i = 0; i < NUM_TRIALS; i++) {
            setGraph();
            int source = RAND.nextInt(N);
            int dest = RAND.nextInt(N);
            GraphSolve a = new GraphSolve(dest);
            Path<Integer> aStar = AStar.solve(a, source);
            Path<Integer> floyd = floyd(source, dest);
            assertEquals(floyd, aStar);
        }
    }
}
