package edu.drexel.cs.jah473.datastructures;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.drexel.cs.jah473.distance.DistanceFunction;
import edu.drexel.cs.jah473.distance.KDPoint;
import edu.drexel.cs.jah473.util.ToNumberFunction;

/**
 * Class to represent a dynamic KD tree data structure. Supports efficient
 * k-nearest neighbor, radius, bounding box, and range searches. Also supports
 * machine learning classification and regression of data points. Insertion,
 * removal, and membership tests are performed in logarithmic time. <br>
 * <br>
 * All data points are stored in leaf nodes. The default maximum number of data
 * points per node is 5, but this can be overridden using the alternate
 * constructor. The maximum may be violated only if the node contains duplicate
 * points. <br>
 * <br>
 * The tree is built in nlog<sup>2</sup>n time using a unique splitting
 * algorithm. Rather than choosing the median of the data set as the splitting
 * point, the algorithm chooses the point which most closely cuts the data set
 * in half. This point may or may not actually be the median. The splitting
 * algorithm avoids creating empty nodes by cycling to a different dimension if
 * it cannot find a point that splits the data. The tree is guaranteed to be
 * relatively balanced after it is initially built, but may become unbalanced
 * after several insertions or deletions. If a series of many insertions and/or
 * deletions are made post-construction, the programmer may wish to rebuild the
 * tree by instantiating a new KDTree object using the updated set of data. <br>
 * <br>
 * This class implements the {@link Collection} interface.
 * 
 * @author Justin Horvitz
 *
 * @param <E>
 *            the type of the data stored in this KD tree, must be subclass of
 *            {@link KDPoint}
 */
public class KDTree<E extends KDPoint> implements Collection<E>, Serializable {

    /* Wraps a data point with a distance value for radius and kNN searches */
    protected static final class Neighbor<E extends KDPoint> implements Comparable<Neighbor<E>> {
        E data;
        double dist;

        public Neighbor(E data, double distance) {
            this.data = data;
            this.dist = distance;
        }

        @Override
        public int compareTo(Neighbor<E> other) {
            return Double.compare(dist, other.dist);
        }
    }

    /* Represents a node in the tree */
    protected static class Node<E extends KDPoint> implements Serializable {

        private static final long serialVersionUID = 2684990727176173177L;

        int dim;
        Node<E> left;
        List<E> members;
        Node<E> right;
        double split;

        boolean isLeaf() {
            return left == null;
        }
    }

    /* Possible overlap types */
    protected static enum Overlap {
        COMPLETE, NONE, PARTIAL
    }

    private static final long serialVersionUID = -5851833806330661148L;

    /* Traverse to all leaf nodes of subtree and add all members */
    protected static final <E extends KDPoint> void express(Node<E> node, List<E> collector) {
        if (node.isLeaf()) {
            collector.addAll(node.members);
            return;
        }
        express(node.left, collector);
        express(node.right, collector);
    }

    /* Finds the leaf node where the point belongs */
    protected static <E extends KDPoint> Node<E> findLeaf(Node<E> current, E point) {
        while (!current.isLeaf()) {
            if (point.getCoord(current.dim) <= current.split) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return current;
    }

    /* Iterate over data points and collect those inside box */
    protected static <E extends KDPoint> void handPick(Node<E> node, double[] box, List<E> collector) {
        final int k = box.length / 2;
        for (E point : node.members) {
            boolean inside = true;
            int index = 0;
            for (int i = 0; i < k; i++) {
                final double coord = point.getCoord(i);
                if (coord < box[index++] || coord > box[index++]) {
                    inside = false;
                    break;
                }
            }
            if (inside) {
                collector.add(point);
            }
        }
    }

    /* Iterate over data points and collect those inside box, for 1 dimension */
    protected static <E extends KDPoint> void handPick1D(Node<E> node, int dim, double[] box, List<E> collector) {
        node.members.stream().filter(p -> p.getCoord(dim) >= box[0] && p.getCoord(dim) <= box[1])
                .forEach(p -> collector.add(p));
    }

    /* Determines the overlap type between box and restrictions */
    protected static final Overlap overlapType(double[] box, double[] restrictions) {
        final int k = box.length / 2;
        boolean subset = true;
        for (int i = 0; i < k; i++) {
            double bmin = box[i * 2];
            double bmax = box[i * 2 + 1];
            double rmin = restrictions[i * 2];
            double rmax = restrictions[i * 2 + 1];
            if (bmin <= rmax && rmin <= bmax) {
                if (subset) {
                    subset = bmin < rmin && bmax > rmax;
                }
            } else {
                return Overlap.NONE;
            }
        }
        if (subset) {
            return Overlap.COMPLETE;
        }
        return Overlap.PARTIAL;
    }

    /* Recursive helper function for bounding box search */
    private static <E extends KDPoint> void pointsInBoxAux(Node<E> node, double[] box, double[] restrictions,
            List<E> collector) {
        final Overlap overlap = overlapType(box, restrictions);
        if (overlap == Overlap.NONE) {
            return;
        }
        if (overlap == Overlap.COMPLETE) {
            express(node, collector);
            return;
        }
        if (node.isLeaf()) {
            handPick(node, box, collector);
            return;
        }
        double[] tighterLeft = restrictions.clone();
        tighterLeft[node.dim * 2 + 1] = node.split;
        pointsInBoxAux(node.left, box, tighterLeft, collector);
        double[] tighterRight = restrictions.clone();
        tighterRight[node.dim * 2] = node.split;
        pointsInBoxAux(node.right, box, tighterRight, collector);
    }

    private static <E extends KDPoint> void pointsInRangeAux(Node<E> node, int dim, double[] box,
            double[] restrictions, List<E> collector) {
        final Overlap overlap = overlapType(box, restrictions);
        if (overlap == Overlap.NONE) {
            return;
        }
        if (overlap == Overlap.COMPLETE) {
            express(node, collector);
            return;
        }
        if (node.isLeaf()) {
            handPick1D(node, dim, box, collector);
            return;
        }
        if (node.dim == dim) {
            double[] tighterLeft = restrictions.clone();
            tighterLeft[1] = node.split;
            pointsInRangeAux(node.left, dim, box, tighterLeft, collector);
            double[] tighterRight = restrictions.clone();
            tighterRight[0] = node.split;
            pointsInRangeAux(node.right, dim, box, tighterRight, collector);
        } else {
            pointsInRangeAux(node.left, dim, box, restrictions, collector);
            pointsInRangeAux(node.right, dim, box, restrictions, collector);
        }

    }

    /* Comparator for the given dimension */
    protected static int sortByDim(KDPoint p1, KDPoint p2, int dimension) {
        return Double.compare(p1.getCoord(dimension), p2.getCoord(dimension));
    }

    protected transient WeakReference<List<E>> dataRef = new WeakReference<>(null);

    protected transient boolean dataUpToDate = false;
    protected DistanceFunction distFunc;
    protected int k;
    protected int pointsPerLeaf = 5;
    protected Node<E> root;
    protected int size = 0;
    /**
     * Constructs a new KD tree.
     * 
     * @param data
     *            a collection of data points
     * @param k
     *            the number of dimensions
     * @param distanceFunction
     *            the formula to use for calculating distance between points
     */
    public KDTree(Collection<E> data, int k, DistanceFunction distanceFunction) {
        init(data, k, distanceFunction);
    }

    /**
     * Constructs a new KD tree with the given number of data points per leaf
     * node.
     * 
     * @param data
     *            a collection of data points
     * @param k
     *            the number of dimensions
     * @param pointsPerLeaf
     *            the maximum number of data points to store per leaf node
     * @param distanceFunction
     *            the formula to use for calculating distance between points
     */
    public KDTree(Collection<E> data, int k, int pointsPerLeaf, DistanceFunction distanceFunction) {
        if (pointsPerLeaf < 1) {
            throw new IllegalArgumentException("Points per leaf must be greater than or equal to 1");
        }
        this.pointsPerLeaf = pointsPerLeaf;
        init(data, k, distanceFunction);
    }

    /**
     * Adds a data point to this tree. The tree supports storage of duplicate
     * points.
     * 
     * @param point
     *            the point to add
     * @return {@code true} (as specified by Collection.add(E))
     */
    @Override
    public boolean add(E point) {
        Node<E> leaf = findLeaf(root, point);
        leaf.members.add(point);
        if (leaf.members.size() > pointsPerLeaf) {
            splitNode(leaf);
        }
        size++;
        if (dataRef.get() != null && dataUpToDate) {
            dataRef.get().add(point);
        }
        return true;
    }

    /**
     * Adds all of the data points in the collection to this tree.
     * 
     * @param points
     *            the data points to add
     * @return {@code true} if this tree changed as a result of the call
     */
    @Override
    public boolean addAll(Collection<? extends E> points) {
        points.stream().forEach(p -> add(p));
        return !points.isEmpty();
    }

    /* Recursively builds the KD Tree */
    protected void buildTree(Node<E> current) {
        final int len = current.members.size();
        if (len <= pointsPerLeaf) {
            return;
        }
        if (splitNode(current)) {
            buildTree(current.left);
            buildTree(current.right);
        }
    }

    /**
     * Calculates the distance between two points, using this tree's distance
     * function.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the distance between point1 and point2
     */
    public double calcDistance(KDPoint point1, KDPoint point2) {
        return distFunc.distanceBetween(point1, point2);
    }

    /**
     * Clears all data from this tree. This tree will be a single empty root
     * node after this call returns.
     */
    @Override
    public void clear() {
        size = 0;
        root = new Node<>();
        root.members = new ArrayList<>();
        dataRef.clear();
        dataUpToDate = false;
    }

    /**
     * Returns true if this tree contains the specified element.
     * 
     * @param o
     *            the element in question
     * @return {@code true} if this tree contains the specified element
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        E point = null;
        try {
            point = (E) o;
        } catch (ClassCastException e) {
            return false;
        }
        Node<E> leaf = findLeaf(root, point);
        return leaf.members.contains(point);
    }

    /**
     * Returns true if this tree contains all of the elements in the specified
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

    /**
     * Gets the number of dimensions in this tree.
     * 
     * @return the number of dimensions in this tree
     */
    public int getK() {
        return k;
    }

    /* Common constructor */
    protected void init(Collection<E> data, int k, DistanceFunction distanceFunction) {
        if (k < 1) {
            throw new IllegalArgumentException("k must be greater than or equal to 1");
        }
        this.k = k;
        this.distFunc = distanceFunction;
        this.size = data.size();
        for (E point : data) {
            if (point.getK() < k) {
                throw new IllegalArgumentException("KDPoint " + point + " has fewer than " + k + " dimensions");
            }
        }
        root = new Node<>();
        root.members = new ArrayList<>(data);
        root.dim = 0;
        buildTree(root);
    }

    /**
     * Returns true if this tree contains no elements.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns an iterator for this tree. The iterator is guaranteed to traverse
     * points in the order they are stored in the tree from left to right,
     * however this ordering is not completely sorted on any particular
     * dimension unless k = 1, in which case the KD tree is essentially a binary
     * search tree.
     */
    @Override
    public Iterator<E> iterator() {
        refreshData();
        return dataRef.get().iterator();
    }

    /**
     * Finds the k nearest neighbors to the given center point.
     * 
     * @param center
     *            the center point
     * @param k
     *            how many neighbors to find
     * @return a list, sorted by distance, of the k data points in this tree
     *         closest to the center point
     */
    public List<E> kNN(KDPoint center, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k cannot be negative");
        }
        if (k == 0) {
            return new ArrayList<>();
        }
        if (k > size) {
            throw new IllegalArgumentException("k is greater than the number of points in this tree");
        }
        Queue<Neighbor<E>> neighbors = new PriorityQueue<>(k, Collections.reverseOrder());
        kNNAux(center, k, Double.POSITIVE_INFINITY, root, center.getCoords(), neighbors);
        return neighbors.stream().sorted().map(n -> n.data).collect(Collectors.toList());
    }

    /* Recursive helper function for kNN search */
    private void kNNAux(KDPoint center, int k, double radius, Node<E> current, double[] restrictions,
            Queue<Neighbor<E>> neighbors) {
        if (current.isLeaf()) {
            for (E point : current.members) {
                double dist = calcDistance(center, point);
                if (neighbors.size() < k) {
                    neighbors.add(new Neighbor<>(point, dist));
                    radius = neighbors.peek().dist;
                } else if (dist < radius) {
                    neighbors.remove();
                    neighbors.add(new Neighbor<>(point, dist));
                    radius = neighbors.peek().dist;
                }

            }
            return;
        }
        final int dim = current.dim;
        boolean wentRight = false;
        if (center.getCoord(dim) <= current.split) {
            kNNAux(center, k, radius, current.left, restrictions, neighbors);
        } else {
            wentRight = true;
            kNNAux(center, k, radius, current.right, restrictions, neighbors);
        }
        radius = neighbors.peek().dist;
        double[] restrictionsCopy = restrictions.clone();
        restrictionsCopy[dim] = current.split;
        double foundDistance = calcDistance(center, new KDPoint(restrictionsCopy));
        if (foundDistance < radius || neighbors.size() < k) {
            if (wentRight) {
                kNNAux(center, k, radius, current.left, restrictionsCopy, neighbors);
            } else {
                kNNAux(center, k, radius, current.right, restrictionsCopy, neighbors);
            }
        }
    }

    /**
     * Performs a majority vote classification for the given point based on its
     * k-nearest neighbors in this tree.
     * 
     * @param point
     *            the point in question
     * @param k
     *            the number of neighbors
     * @param mapper
     *            a {@link Function} of type (? super E {@literal ->} T) mapping
     *            a data point to any type
     * @param <T>
     *            the result type of the mapping function
     * @return a list containing the majority vote winning T value(s)
     */
    public <T> List<T> kNNClassify(KDPoint point, int k, Function<? super E, T> mapper) {
        TallyMap<T> counter = new TallyMap<>();
        kNN(point, k).stream().map(mapper).forEach(t -> counter.increment(t));
        return counter.getMaxKeys();
    }

    /**
     * Performs a mean regression for the given point based on its k-nearest
     * neighbors in this tree.
     * 
     * @param point
     *            the point in question
     * @param k
     *            the number of neighbors
     * @param mapper
     *            a {@link ToNumberFunction} of type (? super E {@literal ->}
     *            Number) mapping a data point to a numeric value
     * @return the average of the mapped values of the k-nearest neighbors, or
     *         {@link Double#NaN} if there was no average to compute
     */
    public double kNNRegression(KDPoint point, int k, ToNumberFunction<? super E, ?> mapper) {
        return kNN(point, k).stream().mapToDouble(p -> mapper.apply(p).doubleValue()).average().orElse(Double.NaN);
    }

    /**
     * Finds all points within the given bounding box. The number of parameters
     * given for the bounding box must be twice the number of dimensions of this
     * tree. The bounding box is inclusive. To construct an exclusive bounding
     * box, consider using {@link Double#MIN_NORMAL}. If there is no restriction
     * on a particular dimension, {@link Double#NEGATIVE_INFINITY} or
     * {@link Double#POSITIVE_INFINITY} may be passed.
     * 
     * @param box
     *            the bounding box in the format minD0, maxD0, minD1, maxD1...
     * @return a list of all points in this tree within the given bounding box
     */
    public List<E> pointsInBox(double... box) {
        int len = box.length;
        if (box.length != k * 2) {
            throw new IllegalArgumentException("wrong number of bounding box parameters");
        }
        List<E> collector = new ArrayList<>();
        double[] restrictions = new double[len];
        for (int i = 0; i < k; i++) {
            restrictions[i * 2] = Double.NEGATIVE_INFINITY;
            restrictions[i * 2 + 1] = Double.POSITIVE_INFINITY;
        }
        pointsInBoxAux(root, box, restrictions, collector);
        return collector;
    }

    /**
     * Finds all points within the given range with respect to the given
     * dimension. The range is inclusive. To construct an exclusive range,
     * consider using {@link Double#MIN_NORMAL}.
     * 
     * @param dimension
     *            the dimension number, between 0 (inclusive) and k (exclusive)
     * @param min
     *            the minimum value of the range (inclusive)
     * @param max
     *            the maximum value of the range (inclusive)
     * @return a list of all points in this tree within the given range for the
     *         given dimension
     */
    public List<E> pointsInRange(int dimension, double min, double max) {
        if (dimension < 0 || dimension >= k) {
            throw new IllegalArgumentException("dimension must be between 0 (inclusive) and k (exclusive)");
        }
        double[] box = new double[2];
        box[0] = min;
        box[1] = max;
        double[] restrictions = new double[2];
        restrictions[0] = Double.NEGATIVE_INFINITY;
        restrictions[1] = Double.POSITIVE_INFINITY;
        List<E> collector = new ArrayList<>();
        pointsInRangeAux(root, dimension, box, restrictions, collector);
        return collector;
    }

    /* Recursive helper function for radius search */
    private void radiusAux(KDPoint center, double radius, Node<E> current, double[] restrictions,
            List<Neighbor<E>> pointsWithin) {
        if (current.isLeaf()) {
            for (E point : current.members) {
                double distance = calcDistance(center, point);
                if (distance <= radius) {
                    pointsWithin.add(new Neighbor<>(point, distance));
                }
            }
            return;
        }
        final int dim = current.dim;
        boolean wentRight = false;
        if (center.getCoord(dim) <= current.split) {
            radiusAux(center, radius, current.left, restrictions, pointsWithin);
        } else {
            wentRight = true;
            radiusAux(center, radius, current.right, restrictions, pointsWithin);
        }
        double[] restrictionsCopy = restrictions.clone();
        restrictionsCopy[dim] = current.split;
        double dist = calcDistance(center, new KDPoint(restrictionsCopy));
        if (dist <= radius) {
            if (wentRight) {
                radiusAux(center, radius, current.left, restrictionsCopy, pointsWithin);
            } else {
                radiusAux(center, radius, current.right, restrictionsCopy, pointsWithin);
            }
        }
    }

    /**
     * Performs a majority vote classification for the given point based on the
     * points in this tree within the given radius from the point in question.
     * 
     * @param point
     *            the point in question
     * @param radius
     *            the radius
     * @param mapper
     *            a {@link Function} of type (? super E {@literal ->} T) mapping
     *            a data point to any type
     * @param <T>
     *            the result type of the mapping function
     * @return a list containing the majority vote winning T value(s)
     */
    public <T> List<T> radiusClassify(KDPoint point, double radius, Function<? super E, T> mapper) {
        TallyMap<T> counter = new TallyMap<>();
        radiusSearch(point, radius).stream().map(mapper).forEach(t -> counter.increment(t));
        return counter.getMaxKeys();
    }

    /**
     * Performs a mean regression for the given point based on the points in
     * this tree within the given radius from the point in question.
     * 
     * @param point
     *            the point in question
     * @param radius
     *            the radius
     * @param mapper
     *            a {@link ToNumberFunction} of type (? super E {@literal ->}
     *            Number) mapping a data point to a numeric value
     * @return the average of the mapped values of the k-nearest neighbors, or
     *         {@link Double#NaN} if there was no average to compute
     */
    public double radiusRegression(KDPoint point, double radius, ToNumberFunction<? super E, ?> mapper) {
        return radiusSearch(point, radius).stream().mapToDouble(p -> mapper.apply(p).doubleValue()).average()
                .orElse(Double.NaN);
    }

    /**
     * Finds all data points in this tree within a certain distance of the given
     * center point.
     * 
     * @param center
     *            the center point
     * @param radius
     *            the radius from the center point
     * @return a list, sorted by distance, of all data points in this tree
     *         within the specified distance from the center point
     */
    public List<E> radiusSearch(KDPoint center, double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius must be greater than or equal to 0");
        }
        List<Neighbor<E>> pointsWithin = new ArrayList<>();
        radiusAux(center, radius, root, center.getCoords(), pointsWithin);
        return pointsWithin.stream().sorted().map(n -> n.data).collect(Collectors.toList());
    }

    /* Read from serialized form */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        dataRef = new WeakReference<>(null);
    }

    /* Refreshes the list of data if necessary */
    protected void refreshData() {
        if (dataRef.get() == null || !dataUpToDate) {
            dataRef = new WeakReference<>(new ArrayList<>());
            express(root, dataRef.get());
            dataUpToDate = true;
        }
    }

    /**
     * Removes one occurrence of the specified element from this tree, if it is
     * present.
     * 
     * @param o
     *            the element to remove
     * @return {@code true} if this tree was changes as a result of this call
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        E point = null;
        try {
            point = (E) o;
        } catch (ClassCastException e) {
            return false;
        }
        Node<E> leaf = findLeaf(root, point);
        if (leaf.members.remove(point)) {
            size--;
            dataUpToDate = false;
            return true;
        }
        return false;
    }

    /**
     * Removes all of the elements in the specified collection from this tree.
     * Duplicates will also be removed.
     * 
     * @param c
     *            the collection of elements to remove
     * @return {@code true} if this tree was changed as a result of this call
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        final int initialSize = size;
        c.stream().forEach(o -> {
            do {
                remove(o);
            } while (contains(o));
        });
        return initialSize != size;
    }

    /**
     * Retains only the elements in this tree that are contained in the
     * specified collection.
     * 
     * @param c
     *            the collection of elements to retain
     * @return {@code true} if this tree was changed as a result of this call
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        final int initialSize = size;
        refreshData();
        removeAll(dataRef.get().stream().filter(p -> !c.contains(p)).collect(Collectors.toList()));
        return initialSize != size;
    }

    /**
     * Returns the number of data points in this tree.
     */
    public int size() {
        return size;
    }

    /*
     * Calls node splitting function at every dimension until node is split or
     * all dimensions have been tried
     */
    protected boolean splitNode(Node<E> leaf) {
        if (splitNodeAux(leaf)) {
            return true;
        }
        final int origDim = leaf.dim;
        leaf.dim++;
        leaf.dim %= k;
        while (leaf.dim != origDim) {
            if (splitNodeAux(leaf)) {
                return true;
            }
            leaf.dim++;
            leaf.dim %= k;
        }
        return false;
    }

    /* Splits a node into two children, if possible */
    private boolean splitNodeAux(Node<E> leaf) {
        List<E> members = leaf.members;
        final int dim = leaf.dim;
        Collections.sort(members, (p1, p2) -> sortByDim(p1, p2, dim));
        final int size = members.size();
        int middle = size / 2;
        double median = members.get(middle - 1).getCoord(dim);
        int middleUp = middle;
        int middleDown = middle - 1;
        while (true) {
            if (middleDown <= 0 || middleUp >= size) {
                return false;
            }
            if (members.get(middleUp).getCoord(dim) > median) {
                middle = middleUp;
                break;
            }
            double downVal = members.get(middleDown).getCoord(dim);
            if (downVal < median) {
                middle = middleDown + 1;
                median = downVal;
                break;
            }
            middleUp++;
            middleDown--;
        }
        leaf.members = null;
        leaf.split = median;
        leaf.left = new Node<>();
        leaf.right = new Node<>();
        final int nextDim = (dim + 1) % k;
        leaf.left.dim = nextDim;
        leaf.right.dim = nextDim;
        leaf.left.members = new ArrayList<>(members.subList(0, middle));
        leaf.right.members = new ArrayList<>(members.subList(middle, size));
        return true;
    }

    /**
     * Returns an array containing all of the elements in this tree.
     * 
     * @return an array containing all of the elements in this tree
     */
    @Override
    public Object[] toArray() {
        refreshData();
        return dataRef.get().toArray();
    }

    /**
     * Returns an array containing all of the elements in this tree. The runtime
     * type of the returned array is that of the specified array. If all of the
     * elements in this tree fit in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this tree.
     * 
     * @param a
     *            the array into which the elements of the list are to be
     *            stored, if it is big enough; otherwise, a new array of the
     *            same runtime type is allocated for this purpose
     * @return an array containing the elements of this tree
     */
    @Override
    public <T> T[] toArray(T[] a) {
        refreshData();
        return dataRef.get().toArray(a);
    }

    /**
     * Gets the data in this tree. Changes to the list returned will not affect
     * this tree. The list returned is not in any particular order.
     * 
     * @return a list of points in this tree.
     */
    public List<E> toList() {
        refreshData();
        return new ArrayList<>(dataRef.get());
    }

}
