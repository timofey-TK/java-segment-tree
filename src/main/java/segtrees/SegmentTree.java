package segtrees;

import java.util.Arrays;

/**
 * A generic segment tree implementation with lazy propagation.
 * @param <T> the type of the values stored in the tree
 * @param <U> the type of the updates to be applied
 */
public class SegmentTree<T, U> {
    private final T[] tree;
    private final U[] lazy;
    private final int n;
    private final Combiner<T> combiner;
    private final Updater<T, U> updater;

    /**
     * Constructs a segment tree from an array of values.
     * @param arr the input array
     * @param combiner the combiner for aggregating values
     * @param updater the updater for applying updates
     */
    @SuppressWarnings("unchecked")
    public SegmentTree(T[] arr, Combiner<T> combiner, Updater<T, U> updater) {
        this.n = arr.length;
        this.combiner = combiner;
        this.updater = updater;
        this.tree = (T[]) new Object[4 * n];
        this.lazy = (U[]) new Object[4 * n];
        Arrays.fill(lazy, updater.neutralUpdate());
        build(arr, 1, 0, n - 1);
    }

    /**
     * Constructs an empty segment tree of size n with default values.
     * @param n the size of the tree
     * @param combiner the combiner for aggregating values
     * @param updater the updater for applying updates
     */
    @SuppressWarnings("unchecked")
    public SegmentTree(int n, Combiner<T> combiner, Updater<T, U> updater) {
        this.n = n;
        this.combiner = combiner;
        this.updater = updater;
        this.tree = (T[]) new Object[4 * n];
        this.lazy = (U[]) new Object[4 * n];
        Arrays.fill(lazy, updater.neutralUpdate());
        Arrays.fill(tree, combiner.neutral());
    }

    private void build(T[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start];
        } else {
            int mid = (start + end) / 2;
            build(arr, 2 * node, start, mid);
            build(arr, 2 * node + 1, mid + 1, end);
            tree[node] = combiner.combine(tree[2 * node], tree[2 * node + 1]);
        }
    }

    private void pushDown(int node, int start, int end) {
        if (!lazy[node].equals(updater.neutralUpdate())) {
            tree[node] = updater.applyUpdate(tree[node], lazy[node], end - start + 1);
            if (start != end) {
                lazy[2 * node] = updater.composeUpdates(lazy[2 * node], lazy[node]);
                lazy[2 * node + 1] = updater.composeUpdates(lazy[2 * node + 1], lazy[node]);
            }
            lazy[node] = updater.neutralUpdate();
        }
    }

    /**
     * Queries the aggregate value over the range [l, r).
     * @param l the left boundary (inclusive)
     * @param r the right boundary (exclusive)
     * @return the aggregate value over the range
     */
    public T query(int l, int r) {
        return query(1, 0, n - 1, l, r - 1);
    }

    private T query(int node, int start, int end, int l, int r) {
        pushDown(node, start, end);
        if (r < start || end < l) {
            return combiner.neutral();
        }
        if (l <= start && end <= r) {
            return tree[node];
        }
        int mid = (start + end) / 2;
        T left = query(2 * node, start, mid, l, r);
        T right = query(2 * node + 1, mid + 1, end, l, r);
        return combiner.combine(left, right);
    }

    /**
     * Applies an update over the range [l, r).
     * @param l the left boundary (inclusive)
     * @param r the right boundary (exclusive)
     * @param update the update to apply
     */
    public void update(int l, int r, U update) {
        update(1, 0, n - 1, l, r - 1, update);
    }

    private void update(int node, int start, int end, int l, int r, U update) {
        pushDown(node, start, end);
        if (r < start || end < l) {
            return;
        }
        if (l <= start && end <= r) {
            lazy[node] = updater.composeUpdates(lazy[node], update);
            pushDown(node, start, end);
            return;
        }
        int mid = (start + end) / 2;
        update(2 * node, start, mid, l, r, update);
        update(2 * node + 1, mid + 1, end, l, r, update);
        tree[node] = combiner.combine(tree[2 * node], tree[2 * node + 1]);
    }
} 