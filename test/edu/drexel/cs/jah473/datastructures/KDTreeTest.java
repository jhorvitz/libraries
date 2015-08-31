package edu.drexel.cs.jah473.datastructures;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.drexel.cs.jah473.datastructures.KDTree;
import edu.drexel.cs.jah473.distance.Distances;
import edu.drexel.cs.jah473.distance.DistanceFunction;
import edu.drexel.cs.jah473.distance.KDPoint;
import edu.drexel.cs.jah473.util.ToNumberFunction;

/*
 * JUnit Tests for KDTree class. All 21 tests passed in 25.1 seconds.
 * Code coverage of KDTree.java was 98.6%, missing only some of the built in
 * methods of the Overlap enum.
 */

public class KDTreeTest {

    static class Neighbor<E extends KDPoint> implements Comparable<Neighbor<E>> {
        E data;
        double dist;

        Neighbor(E data, double dist) {
            this.data = data;
            this.dist = dist;
        }

        @Override
        public int compareTo(Neighbor<E> o) {
            return Double.compare(dist, o.dist);
        }

    }

    static List<DistanceFunction> distFuncs;
    static final List<KDPoint> EMPTY = new ArrayList<>(0);
    static final Comparator<KDPoint> KD_COMPARATOR = new Comparator<KDPoint>() {
        @Override
        public int compare(KDPoint p1, KDPoint p2) {
            int k = p1.getK();
            int d = 0;
            int x;
            do {
                x = Double.compare(p1.getCoord(d), p2.getCoord(d));
            } while (x == 0 && ++d < k);
            return x;
        }
    };
    static List<KDTree<KDPoint>> kdts;
    static List<KDTree<KDPoint>> kdtsSparse;
    static List<List<KDPoint>> listsOfPoints;
    static final int MAX_K = 3;
    static final int NUM_ADDED = 100;
    static final int NUM_DUPLICATES = 200;
    static final int NUM_POINTS = 100_000;
    static final int NUM_REMOVED = 50;
    static final int NUM_TRIALS = 50;

    static final Random RAND = new Random();

    static final int SPACE_BOUND = 100;

    static <E extends KDPoint> List<E> naiveKNN(List<E> points, KDPoint center, int k, DistanceFunction distFunc) {
        return points.stream().map(p -> new Neighbor<E>(p, distFunc.distanceBetween(center, p))).sorted().limit(k)
                .map(n -> n.data).collect(Collectors.toList());
    }

    static <E extends KDPoint, T> List<T> naiveKNNClassify(List<E> points, KDPoint point, int k,
            Function<E, T> mapper, DistanceFunction distFunc) {
        List<T> mapped = naiveKNN(points, point, k, distFunc).stream().map(mapper).collect(Collectors.toList());
        HashMap<T, Integer> counts = new HashMap<>();
        int max = 0;
        for (T t : mapped) {
            Integer count = counts.get(t);
            if (count == null) {
                count = 0;
            }
            count++;
            counts.put(t, count);
            if (count > max) {
                max = count;
            }
        }
        List<T> majorities = new ArrayList<T>();
        for (Map.Entry<T, Integer> entry : counts.entrySet()) {
            int val = entry.getValue();
            if (val == max) {
                majorities.add(entry.getKey());
            }
        }
        return majorities;
    }

    static <E extends KDPoint> double naiveKNNRegression(List<E> points, KDPoint point, int k,
            ToNumberFunction<E, ?> mapper, DistanceFunction distFunc) {
        return naiveKNN(points, point, k, distFunc).stream().mapToDouble(p -> mapper.apply(p).doubleValue()).average()
                .orElse(Double.NaN);
    }

    static <E extends KDPoint> List<E> naivePointsInBox(List<E> points, double[] box) {
        List<E> pointsWithin = new ArrayList<>();
        for (E p : points) {
            int k = p.getK();
            boolean inside = true;
            for (int i = 0; i < k; i++) {
                if (p.getCoord(i) < box[i * 2] || p.getCoord(i) > box[i * 2 + 1]) {
                    inside = false;
                    break;
                }
            }
            if (inside) {
                pointsWithin.add(p);
            }
        }
        return pointsWithin;
    }

    static <E extends KDPoint> List<E> naivePointsInRange(List<E> points, int dim, double min, double max) {
        return points.stream().filter(p -> p.getCoord(dim) >= min && p.getCoord(dim) <= max)
                .collect(Collectors.toList());
    }

    static <E extends KDPoint, T> List<T> naiveRadiusClassify(List<E> points, KDPoint point, double radius,
            Function<E, T> mapper, DistanceFunction distFunc) {
        List<T> mapped = naiveRadiusSearch(points, point, radius, distFunc).stream().map(mapper)
                .collect(Collectors.toList());
        HashMap<T, Integer> counts = new HashMap<>();
        int max = 0;
        for (T t : mapped) {
            Integer count = counts.get(t);
            if (count == null) {
                count = 0;
            }
            count++;
            counts.put(t, count);
            if (count > max) {
                max = count;
            }
        }
        List<T> majorities = new ArrayList<T>();
        for (Map.Entry<T, Integer> entry : counts.entrySet()) {
            int val = entry.getValue();
            if (val == max) {
                majorities.add(entry.getKey());
            }
        }
        return majorities;
    }

    static <E extends KDPoint> double naiveRadiusRegression(List<E> points, KDPoint point, double radius,
            ToNumberFunction<E, ?> mapper, DistanceFunction distFunc) {
        return naiveRadiusSearch(points, point, radius, distFunc).stream()
                .mapToDouble(p -> mapper.apply(p).doubleValue()).average().orElse(Double.NaN);
    }

    static <E extends KDPoint> List<E> naiveRadiusSearch(List<E> points, KDPoint center, double radius,
            DistanceFunction distFunc) {
        return points.stream().map(p -> new Neighbor<E>(p, distFunc.distanceBetween(center, p)))
                .filter(n -> n.dist <= radius).sorted().map(n -> n.data).collect(Collectors.toList());
    }

    static double randCoord() {
        double c = RAND.nextDouble() * SPACE_BOUND;
        return RAND.nextBoolean() ? c : c * -1;
    }

    static KDPoint randKDPoint(int k) {
        double[] coords = new double[k];
        for (int i = 0; i < k; i++) {
            coords[i] = randCoord();
        }
        return new KDPoint(coords);
    }

    @BeforeClass
    public static void setUpClass() {
        kdts = new ArrayList<>(MAX_K);
        kdtsSparse = new ArrayList<>(MAX_K);
        listsOfPoints = new ArrayList<>(MAX_K);
        distFuncs = new ArrayList<>(MAX_K);
        for (int i = 0; i < MAX_K; i++) {
            final int dim = i + 1;
            final DistanceFunction distFunc = Distances.sqEuclidean(dim);
            List<KDPoint> listOfPoints = new ArrayList<>(NUM_POINTS);
            for (int j = 0; j < NUM_POINTS; j++) {
                listOfPoints.add(randKDPoint(dim));
            }
            KDPoint toCopy = listOfPoints.get(RAND.nextInt(listOfPoints.size()));
            for (int j = 0; j < NUM_DUPLICATES; j++) {
                listOfPoints.add(new KDPoint(toCopy.getCoords()));
                for (int k = 0; k < dim; k++) {
                    double[] coords = new double[dim];
                    for (int l = 0; l < dim; l++) {
                        coords[l] = randCoord();
                    }
                    coords[k] = toCopy.getCoord(k);
                    listOfPoints.add(new KDPoint(coords));
                }
            }
            KDTree<KDPoint> kdt = new KDTree<>(listOfPoints, dim, distFunc);
            KDTree<KDPoint> kdtSparse = new KDTree<>(listOfPoints, dim, 1, distFunc);
            for (int j = 0; j < NUM_ADDED; j++) {
                KDPoint point = randKDPoint(dim);
                kdt.add(point);
                kdtSparse.add(point);
                listOfPoints.add(point);
            }
            for (int j = 0; j < NUM_REMOVED; j++) {
                KDPoint toRemove = listOfPoints.get(RAND.nextInt(listOfPoints.size()));
                kdt.remove(toRemove);
                kdtSparse.remove(toRemove);
                listOfPoints.remove(toRemove);
            }
            listsOfPoints.add(listOfPoints);
            kdts.add(kdt);
            kdtsSparse.add(kdtSparse);
            distFuncs.add(distFunc);
        }
    }

    @Test
    public void addAllTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            assertFalse(kdt.addAll(EMPTY));
            assertFalse(kdtSparse.addAll(EMPTY));
            assertFalse(listOfPoints.addAll(EMPTY));
            List<KDPoint> toAdd = new ArrayList<>(NUM_TRIALS);
            for (int i = 0; i < NUM_TRIALS; i++) {
                toAdd.add(randKDPoint(dim));
            }
            final int origSize = kdt.size();
            assertEquals(origSize, kdtSparse.size());
            assertEquals(origSize, listOfPoints.size());
            assertTrue(kdt.addAll(toAdd));
            assertTrue(kdtSparse.addAll(toAdd));
            assertTrue(listOfPoints.addAll(toAdd));
            final int expectedSize = origSize + toAdd.size();
            assertEquals(expectedSize, kdt.size());
            assertEquals(expectedSize, kdtSparse.size());
            assertEquals(expectedSize, listOfPoints.size());
            assertTrue(kdt.containsAll(toAdd));
            assertTrue(kdtSparse.containsAll(toAdd));
            assertTrue(listOfPoints.containsAll(toAdd));
        }
    }

    @Test
    public void addTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            for (int i = 0; i < NUM_TRIALS; i++) {
                final int origSize = kdt.size();
                assertEquals(origSize, kdtSparse.size());
                assertEquals(origSize, listOfPoints.size());
                KDPoint point = randKDPoint(dim);
                assertTrue(kdt.add(point));
                assertTrue(kdtSparse.add(point));
                assertTrue(listOfPoints.add(point));
                assertTrue(kdt.contains(point));
                assertTrue(kdtSparse.contains(point));
                assertTrue(listOfPoints.contains(point));
                final int expectedSize = origSize + 1;
                assertEquals(expectedSize, kdt.size());
                assertEquals(expectedSize, kdtSparse.size());
                assertEquals(expectedSize, listOfPoints.size());
            }
        }
    }

    @Test
    public void clearTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            KDTree<KDPoint> kdt2 = new KDTree<>(kdt, dim, Distances.sqEuclidean(dim));
            KDTree<KDPoint> kdtSparse2 = new KDTree<>(kdtSparse, dim, 1, Distances.sqEuclidean(dim));
            kdt2.clear();
            kdtSparse2.clear();
            assertTrue(kdt2.isEmpty());
            assertTrue(kdtSparse2.isEmpty());
            assertTrue(kdt2.toList().isEmpty());
            assertTrue(kdtSparse2.toList().isEmpty());
        }
    }

    @Test
    public void containsTest() {
        String notKDPoint = "I am not a KD point";
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            assertFalse(kdt.contains(notKDPoint));
            assertFalse(kdtSparse.contains(notKDPoint));
            assertFalse(listOfPoints.contains(notKDPoint));
            for (int i = 0; i < NUM_TRIALS; i++) {
                KDPoint randomPoint = listOfPoints.get(RAND.nextInt(listOfPoints.size()));
                boolean expected = listOfPoints.contains(randomPoint);
                boolean actual = kdt.contains(randomPoint);
                boolean actual2 = kdtSparse.contains(randomPoint);
                assertEquals(expected, actual);
                assertEquals(expected, actual2);
            }
            for (int i = 0; i < NUM_TRIALS; i++) {
                KDPoint randomPoint = randKDPoint(dim);
                boolean expected = listOfPoints.contains(randomPoint);
                boolean actual = kdt.contains(randomPoint);
                boolean actual2 = kdtSparse.contains(randomPoint);
                assertEquals(expected, actual);
                assertEquals(expected, actual2);
            }
        }
    }

    @Test
    public void emptyStartTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            DistanceFunction distFunc = distFuncs.get(z);
            KDTree<KDPoint> kdt = new KDTree<>(EMPTY, dim, distFunc);
            KDTree<KDPoint> kdtSparse = new KDTree<>(EMPTY, dim, 1, distFunc);
            List<KDPoint> listOfPoints = new ArrayList<>(EMPTY);
            assertTrue(kdt.isEmpty());
            assertTrue(kdtSparse.isEmpty());
            assertTrue(listOfPoints.isEmpty());
            KDPoint point = randKDPoint(dim);
            kdt.add(point);
            kdtSparse.add(point);
            listOfPoints.add(point);
            assertTrue(kdt.contains(point));
            assertTrue(kdtSparse.contains(point));
            assertTrue(listOfPoints.contains(point));
            assertEquals(listOfPoints.size(), kdt.size());
            assertEquals(listOfPoints.size(), kdtSparse.size());
            assertTrue(kdt.remove(point));
            assertTrue(kdtSparse.remove(point));
            assertTrue(listOfPoints.remove(point));
            assertTrue(kdt.isEmpty());
            assertTrue(kdtSparse.isEmpty());
            assertTrue(listOfPoints.isEmpty());
            for (int i = 0; i < NUM_TRIALS; i++) {
                int op = RAND.nextInt(10);
                if (listOfPoints.isEmpty()) {
                    op = 0;
                }
                if (op < 4) {
                    point = randKDPoint(dim);
                    assertTrue(kdt.add(point));
                    assertTrue(kdtSparse.add(point));
                    assertTrue(listOfPoints.add(point));
                } else if (op < 6) {
                    point = randKDPoint(dim);
                    boolean actual = kdt.remove(point);
                    boolean actual2 = kdtSparse.remove(point);
                    boolean expected = listOfPoints.remove(point);
                    assertEquals(expected, actual);
                    assertEquals(expected, actual2);
                } else {
                    point = listOfPoints.get(RAND.nextInt(listOfPoints.size()));
                    assertTrue(kdt.remove(point));
                    assertTrue(kdtSparse.remove(point));
                    assertTrue(listOfPoints.remove(point));
                }
                List<KDPoint> kdtList = kdt.toList();
                List<KDPoint> kdtSparseList = kdtSparse.toList();
                List<KDPoint> listOfPointsList = new ArrayList<>(listOfPoints);
                Collections.sort(kdtList, KD_COMPARATOR);
                Collections.sort(kdtSparseList, KD_COMPARATOR);
                Collections.sort(listOfPointsList, KD_COMPARATOR);
                assertEquals(listOfPointsList, kdtList);
                assertEquals(listOfPointsList, kdtSparseList);
            }
        }
    }

    @Test
    public void errorConditionsTest() {
        int num_points = 8;
        List<KDPoint> points2D = new ArrayList<>(num_points);
        for (int i = 0; i < num_points; i++) {
            points2D.add(randKDPoint(2));
        }
        KDPoint badPoint = randKDPoint(1);
        points2D.add(badPoint);
        boolean caught = false;
        try {
            new KDTree<KDPoint>(points2D, 2, Distances.euclidean(2));
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);
        points2D.remove(badPoint);
        caught = false;
        try {
            new KDTree<KDPoint>(points2D, 0, Distances.euclidean(2));
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);
        caught = false;
        try {
            new KDTree<KDPoint>(points2D, 2, 0, Distances.euclidean(2));
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);
    }

    @Test
    public void getKTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            assertEquals(dim, kdts.get(z).getK());
            assertEquals(dim, kdtsSparse.get(z).getK());
        }
    }

    @Test
    public void isEmptyTest() {
        for (int z = 0; z < MAX_K; z++) {
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            assertEquals(kdt.size() == 0, kdt.isEmpty());
            assertEquals(kdtSparse.size() == 0, kdtSparse.isEmpty());
            assertEquals(listOfPoints.size() == 0, listOfPoints.isEmpty());
        }
        KDTree<KDPoint> emptyKDT = new KDTree<>(EMPTY, 2, Distances.euclidean(2));
        assertTrue(EMPTY.isEmpty());
        assertTrue(emptyKDT.isEmpty());
    }

    @Test
    public void kNNClassifyTest() {
        Function<KDPoint, Integer> mapper = new Function<KDPoint, Integer>() {
            @Override
            public Integer apply(KDPoint point) {
                return ((int) Math.abs(point.getCoord(0))) / (SPACE_BOUND / 10);
            }
        };
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            DistanceFunction distFunc = distFuncs.get(z);
            for (int i = 0; i < NUM_TRIALS; i++) {
                KDPoint point = randKDPoint(dim);
                int k = RAND.nextInt((int) Math.log(NUM_POINTS)) + 1;
                List<Integer> actual = kdt.kNNClassify(point, k, mapper);
                List<Integer> actual2 = kdtSparse.kNNClassify(point, k, mapper);
                List<Integer> expected = naiveKNNClassify(listOfPoints, point, k, mapper, distFunc);
                Collections.sort(actual);
                Collections.sort(actual2);
                Collections.sort(expected);
                assertEquals(expected, actual);
                assertEquals(expected, actual2);
            }
        }
    }

    @Test
    public void kNNRegressionTest() {
        ToNumberFunction<KDPoint, Double> mapper = new ToNumberFunction<KDPoint, Double>() {
            @Override
            public Double apply(KDPoint point) {
                final int k = point.getK();
                double total = 0;
                for (int i = 0; i < k; i++) {
                    total += point.getCoord(i);
                }
                return total / k;
            }
        };
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            DistanceFunction distFunc = distFuncs.get(z);
            for (int i = 0; i < NUM_TRIALS; i++) {
                KDPoint point = randKDPoint(dim);
                int k = RAND.nextInt((int) Math.log(NUM_POINTS)) + 1;
                double actual = kdt.kNNRegression(point, k, mapper);
                double actual2 = kdtSparse.kNNRegression(point, k, mapper);
                double expected = naiveKNNRegression(listOfPoints, point, k, mapper, distFunc);
                assertEquals(expected, actual, 0);
                assertEquals(expected, actual2, 0);
            }
        }
    }

    @Test
    public void kNNTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            DistanceFunction distFunc = distFuncs.get(z);
            KDPoint center = randKDPoint(dim);
            boolean caught = false;
            try {
                kdt.kNN(center, -1);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught);
            caught = false;
            try {
                kdt.kNN(center, listOfPoints.size() + 1);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught);
            List<KDPoint> noNeighbors = kdt.kNN(center, 0);
            assertTrue(noNeighbors.isEmpty());
            for (int i = 0; i < NUM_TRIALS; i++) {
                int k = RAND.nextInt((int) Math.log(NUM_POINTS)) + 1;
                List<KDPoint> expected = naiveKNN(listOfPoints, center, k, distFunc);
                List<KDPoint> actual = kdt.kNN(center, k);
                List<KDPoint> actual2 = kdtSparse.kNN(center, k);
                assertEquals(expected, actual);
                assertEquals(expected, actual2);
            }
        }
    }

    @Test
    public void pointsInBoxTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            boolean caught = false;
            try {
                kdt.pointsInBox(1, 2, 3);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught);
            double[] box = new double[dim * 2];
            for (int i = 0; i < dim; i++) {
                box[i * 2] = Double.NEGATIVE_INFINITY;
                box[i * 2 + 1] = Double.POSITIVE_INFINITY;
            }
            List<KDPoint> allPoints = kdt.pointsInBox(box);
            Collections.sort(allPoints, KD_COMPARATOR);
            assertEquals(listOfPoints, allPoints);
            for (int i = 0; i < NUM_TRIALS; i++) {
                for (int j = 0; j < box.length; j++) {
                    box[j] = randCoord();
                }
                List<KDPoint> expected = naivePointsInBox(listOfPoints, box);
                List<KDPoint> actual = kdt.pointsInBox(box);
                List<KDPoint> actual2 = kdtSparse.pointsInBox(box);
                Collections.sort(actual, KD_COMPARATOR);
                Collections.sort(actual2, KD_COMPARATOR);
                assertEquals(expected, actual);
                assertEquals(expected, actual2);
            }
        }
    }

    @Test
    public void pointsInRangeTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            boolean caught = false;
            try {
                kdt.pointsInRange(-1, randCoord(), randCoord());
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught);
            caught = false;
            try {
                kdt.pointsInRange(dim + 1, randCoord(), randCoord());
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught);
            for (int i = 0; i < NUM_TRIALS; i++) {
                int randDim = RAND.nextInt(dim);
                double min = randCoord();
                double max = randCoord();
                List<KDPoint> expected = naivePointsInRange(listOfPoints, randDim, min, max);
                List<KDPoint> actual = kdt.pointsInRange(randDim, min, max);
                List<KDPoint> actual2 = kdtSparse.pointsInRange(randDim, min, max);
                Collections.sort(actual, KD_COMPARATOR);
                Collections.sort(actual2, KD_COMPARATOR);
                assertEquals(expected, actual);
                assertEquals(expected, actual2);
            }
        }
    }

    @Test
    public void radiusClassifyTest() {
        Function<KDPoint, Integer> mapper = new Function<KDPoint, Integer>() {
            @Override
            public Integer apply(KDPoint point) {
                return ((int) Math.abs(point.getCoord(0))) / (SPACE_BOUND / 10);
            }
        };
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            DistanceFunction distFunc = distFuncs.get(z);
            for (int i = 0; i < NUM_TRIALS; i++) {
                KDPoint point = randKDPoint(dim);
                double radius = RAND.nextDouble() * Math.log(SPACE_BOUND);
                List<Integer> actual = kdt.radiusClassify(point, radius, mapper);
                List<Integer> actual2 = kdtSparse.radiusClassify(point, radius, mapper);
                List<Integer> expected = naiveRadiusClassify(listOfPoints, point, radius, mapper, distFunc);
                Collections.sort(actual);
                Collections.sort(actual2);
                Collections.sort(expected);
                assertEquals(expected, actual);
                assertEquals(expected, actual2);
            }
        }
    }

    @Test
    public void radiusRegressionTest() {
        ToNumberFunction<KDPoint, Double> mapper = new ToNumberFunction<KDPoint, Double>() {
            @Override
            public Double apply(KDPoint point) {
                final int k = point.getK();
                double total = 0;
                for (int i = 0; i < k; i++) {
                    total += point.getCoord(i);
                }
                return total / k;
            }
        };
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            DistanceFunction distFunc = distFuncs.get(z);
            for (int i = 0; i < NUM_TRIALS; i++) {
                KDPoint point = randKDPoint(dim);
                double radius = RAND.nextDouble() * Math.log(SPACE_BOUND);
                double actual = kdt.radiusRegression(point, radius, mapper);
                double actual2 = kdtSparse.radiusRegression(point, radius, mapper);
                double expected = naiveRadiusRegression(listOfPoints, point, radius, mapper, distFunc);
                assertEquals(expected, actual, 0);
                assertEquals(expected, actual2, 0);
            }
        }
    }

    @Test
    public void radiusSearchTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            DistanceFunction distFunc = distFuncs.get(z);
            KDPoint center = randKDPoint(dim);
            boolean caught = false;
            try {
                kdt.radiusSearch(center, -1);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught);
            for (int i = 0; i < NUM_TRIALS; i++) {
                double radius = RAND.nextDouble() * Math.log(SPACE_BOUND);
                List<KDPoint> expected = naiveRadiusSearch(listOfPoints, center, radius, distFunc);
                List<KDPoint> actual = kdt.radiusSearch(center, radius);
                List<KDPoint> actual2 = kdtSparse.radiusSearch(center, radius);
                assertEquals(expected, actual);
                assertEquals(expected, actual2);
            }
        }
    }

    @Test
    public void removeAllTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            List<KDPoint> toRemove = new ArrayList<>(NUM_TRIALS);
            for (int i = 0; i < NUM_TRIALS; i++) {
                KDPoint point = randKDPoint(dim);
                if (!kdt.contains(point))
                    ;
                toRemove.add(point);
            }
            assertFalse(toRemove.isEmpty());
            final int origSize = kdt.size();
            assertEquals(origSize, kdtSparse.size());
            assertEquals(origSize, listOfPoints.size());
            assertFalse(kdt.removeAll(toRemove));
            assertFalse(kdtSparse.removeAll(toRemove));
            assertFalse(listOfPoints.removeAll(toRemove));
            assertEquals(origSize, kdt.size());
            assertEquals(origSize, kdtSparse.size());
            assertEquals(origSize, listOfPoints.size());
            toRemove.clear();
            for (int i = 0; i < NUM_TRIALS; i++) {
                toRemove.add(listOfPoints.get(RAND.nextInt(listOfPoints.size())));
                toRemove.add(randKDPoint(dim));
            }
            assertFalse(kdt.containsAll(toRemove));
            assertTrue(kdt.removeAll(toRemove));
            assertTrue(kdtSparse.removeAll(toRemove));
            assertTrue(listOfPoints.removeAll(toRemove));
            assertEquals(listOfPoints.size(), kdt.size());
            assertEquals(listOfPoints.size(), kdtSparse.size());
        }
    }

    @Test
    public void removeTest() {
        String notKDPoint = "I am not a KD point";
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            assertFalse(kdt.remove(notKDPoint));
            assertFalse(kdtSparse.remove(notKDPoint));
            assertFalse(listOfPoints.remove(notKDPoint));
            for (int i = 0; i < NUM_TRIALS; i++) {
                final int origSize = kdt.size();
                assertEquals(origSize, kdtSparse.size());
                assertEquals(origSize, listOfPoints.size());
                KDPoint point = randKDPoint(dim);
                boolean contains = kdt.contains(point);
                assertEquals(contains, kdtSparse.contains(point));
                assertEquals(contains, listOfPoints.contains(point));
                assertEquals(contains, kdt.remove(point));
                assertEquals(contains, kdtSparse.remove(point));
                assertEquals(contains, listOfPoints.remove(point));
                final int expectedSize = origSize - (contains ? 1 : 0);
                assertEquals(expectedSize, kdt.size());
                assertEquals(expectedSize, kdtSparse.size());
                assertEquals(expectedSize, listOfPoints.size());
            }
            for (int i = 0; i < NUM_TRIALS; i++) {
                final int origSize = kdt.size();
                assertEquals(origSize, kdtSparse.size());
                assertEquals(origSize, listOfPoints.size());
                KDPoint point = listOfPoints.get(RAND.nextInt(listOfPoints.size()));
                assertTrue(kdt.contains(point));
                assertTrue(kdtSparse.contains(point));
                assertTrue(listOfPoints.contains(point));
                assertTrue(kdt.remove(point));
                assertTrue(kdtSparse.remove(point));
                assertTrue(listOfPoints.remove(point));
                final int expectedSize = origSize - 1;
                assertEquals(expectedSize, kdt.size());
                assertEquals(expectedSize, kdtSparse.size());
                assertEquals(expectedSize, listOfPoints.size());
            }
        }
    }

    @Test
    public void retainAllTest() {
        for (int z = 0; z < MAX_K; z++) {
            final int dim = z + 1;
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            Set<KDPoint> setOfPoints = new HashSet<>(listOfPoints);
            assertFalse(kdt.retainAll(setOfPoints));
            assertFalse(kdtSparse.retainAll(setOfPoints));
            assertFalse(listOfPoints.retainAll(setOfPoints));
            assertEquals(listOfPoints.size(), kdt.size());
            assertEquals(listOfPoints.size(), kdtSparse.size());
            final int retainSize = NUM_POINTS / 2;
            Set<KDPoint> toRetain = new HashSet<>(retainSize);
            for (int i = 0; i < retainSize; i++) {
                toRetain.add(listOfPoints.get(RAND.nextInt(listOfPoints.size())));
            }
            KDTree<KDPoint> kdt2 = new KDTree<>(kdt, dim, Distances.sqEuclidean(dim));
            KDTree<KDPoint> kdtSparse2 = new KDTree<>(kdtSparse, dim, 1, Distances.sqEuclidean(dim));
            assertTrue(toRetain.size() < kdt.size());
            assertTrue(kdt2.retainAll(toRetain));
            assertTrue(kdtSparse2.retainAll(toRetain));
            assertTrue(setOfPoints.retainAll(toRetain));
            List<KDPoint> kdtList = new ArrayList<>(new HashSet<>(kdt2));
            List<KDPoint> kdtSparseList = new ArrayList<>(new HashSet<>(kdtSparse2));
            List<KDPoint> setOfPointsList = new ArrayList<>(setOfPoints);
            Collections.sort(kdtList, KD_COMPARATOR);
            Collections.sort(kdtSparseList, KD_COMPARATOR);
            Collections.sort(setOfPointsList, KD_COMPARATOR);
            assertEquals(setOfPointsList, kdtList);
            assertEquals(setOfPointsList, kdtSparseList);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void serializeTest() throws FileNotFoundException, IOException, ClassNotFoundException {
        List<KDTree<KDPoint>> olds = new ArrayList<>(MAX_K);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("out.dat"));
        for (int z = 0; z < MAX_K; z++) {
            KDTree<KDPoint> kdt = kdts.get(z);
            oos.writeObject(kdt);
            olds.add(kdt);
        }
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("out.dat"));
        for (int z = 0; z < MAX_K; z++) {
            KDTree<KDPoint> read = (KDTree<KDPoint>) ois.readObject();
            kdts.set(z, read);
            KDTree<KDPoint> old = olds.get(z);
            assertEquals(old.size(), read.size());
            assertEquals(old.getK(), read.getK());
        }
        ois.close();
    }

    @Before
    public void setUp() throws FileNotFoundException, ClassNotFoundException, IOException {
        for (int z = 0; z < MAX_K; z++) {
            Collections.sort(listsOfPoints.get(z), KD_COMPARATOR);
        }
    }

    @Test
    public void sizeTest() {
        for (int z = 0; z < MAX_K; z++) {
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            int actual = kdt.size();
            int actual2 = kdtSparse.size();
            int expected = listOfPoints.size();
            assertEquals(expected, actual);
            assertEquals(expected, actual2);
        }
    }

    @Test
    public void toArrayTest() {
        for (int z = 0; z < MAX_K; z++) {
            KDTree<KDPoint> kdt = kdts.get(z);
            KDTree<KDPoint> kdtSparse = kdtsSparse.get(z);
            List<KDPoint> listOfPoints = listsOfPoints.get(z);
            KDPoint[] dummy = new KDPoint[0];
            KDPoint[] actual = kdt.toArray(dummy);
            KDPoint[] actual2 = kdtSparse.toArray(dummy);
            KDPoint[] expected = listOfPoints.toArray(dummy);
            Arrays.sort(actual, KD_COMPARATOR);
            Arrays.sort(actual2, KD_COMPARATOR);
            assertArrayEquals(expected, actual);
            assertArrayEquals(expected, actual2);
        }
    }

}
