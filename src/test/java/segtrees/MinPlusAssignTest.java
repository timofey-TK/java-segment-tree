package segtrees;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinPlusAssignTest {

    private static class MinCombiner implements Combiner<Long> {
        @Override
        public Long combine(Long left, Long right) {
            return Math.min(left, right);
        }

        @Override
        public Long neutral() {
            return Long.MAX_VALUE;
        }
    }

    private static class AssignUpdater implements Updater<Long, Long> {
        @Override
        public Long applyUpdate(Long value, Long update, int rangeSize) {
            return update == Long.MAX_VALUE ? value : update;
        }

        @Override
        public Long composeUpdates(Long current, Long next) {
            return next == Long.MAX_VALUE ? current : next;
        }

        @Override
        public Long neutralUpdate() {
            return Long.MAX_VALUE;
        }
    }

    @Test
    void smallFixedScenario() {
        Long[] a = {1L, 2L, 3L, 4L, 5L};
        SegmentTree<Long, Long> st = new SegmentTree<>(a, new MinCombiner(), new AssignUpdater());
        assertEquals(2, st.query(1, 4)); // min of 2,3,4
        st.update(0, 5, 3L); // assign 3 to all
        assertEquals(3, st.query(0, 5)); // min is 3
        st.update(2, 4, 1L); // assign 1 to indices 2 and 3
        assertEquals(1, st.query(1, 5)); // min of 3,1,1,3
        assertEquals(3, st.query(0, 2)); // min of 3,3
        st.update(0, 1, 0L); // assign 0 to index 0
        assertEquals(0, st.query(0, 2)); // min of 0,3
    }

    @Test
    void pointRange() {
        Long[] a = {1L, 2L, 3L, 4L, 5L};
        SegmentTree<Long, Long> st = new SegmentTree<>(a, new MinCombiner(), new AssignUpdater());
        assertEquals(3, st.query(2, 3)); // just 3
        st.update(2, 3, 5L); // assign 5 to index 2
        assertEquals(5, st.query(2, 3)); // now 5
        st.update(2, 3, 2L); // assign 2 to index 2
        assertEquals(2, st.query(2, 3)); // now 2
    }

    @Test
    void fullRange() {
        Long[] a = {1L, 2L, 3L, 4L, 5L};
        SegmentTree<Long, Long> st = new SegmentTree<>(a, new MinCombiner(), new AssignUpdater());
        assertEquals(1, st.query(0, 5)); // min of all
        st.update(0, 5, 10L); // assign 10 to all
        assertEquals(10, st.query(0, 5)); // min is 10
    }

    @Test
    void boundaryRanges() {
        Long[] a = {1L, 2L, 3L, 4L, 5L};
        SegmentTree<Long, Long> st = new SegmentTree<>(a, new MinCombiner(), new AssignUpdater());
        assertEquals(1, st.query(0, 1)); // left boundary
        assertEquals(5, st.query(4, 5)); // right boundary
        st.update(0, 1, 0L); // assign 0 to left boundary
        assertEquals(0, st.query(0, 1));
        st.update(4, 5, 6L); // assign 6 to right boundary
        assertEquals(6, st.query(4, 5));
    }

    @Test
    void consecutiveUpdates() {
        Long[] a = {1L, 2L, 3L, 4L, 5L};
        SegmentTree<Long, Long> st = new SegmentTree<>(a, new MinCombiner(), new AssignUpdater());
        st.update(1, 4, 10L); // assign 10 to 1,2,3
        st.update(2, 5, 2L); // assign 2 to 2,3,4
        assertEquals(1, st.query(0, 5)); // min of 1,10,2,2,2
    }

    @Test
    void randomOperations() {
        Random rnd = new Random(12345);
        int n = rnd.nextInt(1000) + 1;
        Long[] a = new Long[n];
        for (int i = 0; i < n; i++) {
            a[i] = (long) rnd.nextInt(100);
        }
        MinCombiner minCombiner = new MinCombiner();
        AssignUpdater assignUpdater = new AssignUpdater();
        SegmentTree<Long, Long> st = new SegmentTree<>(a, minCombiner, assignUpdater);
        NaiveArray<Long, Long> naive = new NaiveArray<>(a, minCombiner, assignUpdater);
        for (int i = 0; i < 100; i++) {
            if (rnd.nextBoolean()) {
                int l = rnd.nextInt(n);
                int r = rnd.nextInt(n - l) + l + 1;
                long update = rnd.nextLong(100);
                st.update(l, r, update);
                naive.update(l, r, update);
            } else {
                int l = rnd.nextInt(n);
                int r = rnd.nextInt(n - l) + l + 1;
                assertEquals(naive.query(l, r), st.query(l, r));
            }
        }
    }

    private static class NaiveArray<T, U> {
        private final T[] a;
        private final Updater<T, U> updater;
        private final Combiner<T> combiner;

        NaiveArray(T[] arr, Combiner<T> combiner, Updater<T, U> updater) {
            this.a = arr.clone();
            this.updater = updater;
            this.combiner = combiner;
        }

        void update(int l, int r, U update) {
            for (int i = l; i < r; i++) {
                a[i] = updater.applyUpdate(a[i], update, 1);
            }
        }

        T query(int l, int r) {
            T result = a[l];
            for (int i = l + 1; i < r; i++) {
                result = combiner.combine(result, a[i]);
            }
            return result;
        }
    }
} 