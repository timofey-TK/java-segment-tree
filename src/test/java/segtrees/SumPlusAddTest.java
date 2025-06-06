package segtrees;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SumPlusAddTest {

    private static class SumCombiner implements Combiner<Long> {
        @Override
        public Long combine(Long left, Long right) {
            return left + right;
        }

        @Override
        public Long neutral() {
            return 0L;
        }
    }

    private static class AddUpdater implements Updater<Long, Long> {
        @Override
        public Long applyUpdate(Long value, Long update, int rangeSize) {
            return value + update * rangeSize;
        }

        @Override
        public Long composeUpdates(Long current, Long next) {
            return current + next;
        }

        @Override
        public Long neutralUpdate() {
            return 0L;
        }
    }

    @Test
    void smallFixedScenario() {
        Long[] a = {1L, 2L, 3L, 4L, 5L};
        SegmentTree<Long, Long> st = new SegmentTree<>(a, new SumCombiner(), new AddUpdater());
        assertEquals(9, st.query(1, 4)); // 2+3+4
        st.update(0, 5, 3L); // +3 to all
        assertEquals(26, st.query(1, 5)); // 5+6+7+8
        st.update(2, 4, 2L); // +2 to indices 2 and 3
        assertEquals(30, st.query(1, 5)); // 5+8+9+8
        assertEquals(9, st.query(0, 2)); // 4+5
        st.update(0, 1, 1L); // +1 to index 0
        assertEquals(10, st.query(0, 2)); // 5+5
    }

    @Test
    void pointRange() {
        Long[] a = {1L, 2L, 3L, 4L, 5L};
        SegmentTree<Long, Long> st = new SegmentTree<>(a, new SumCombiner(), new AddUpdater());
        assertEquals(3, st.query(2, 3)); // just 3
        st.update(2, 3, 5L); // +5 to index 2
        assertEquals(8, st.query(2, 3)); // now 8
        st.update(2, 3, -3L); // -3 to index 2
        assertEquals(5, st.query(2, 3)); // now 5
    }

    @Test
    void fullRange() {
        Long[] a = {1L, 2L, 3L, 4L, 5L};
        SegmentTree<Long, Long> st = new SegmentTree<>(a, new SumCombiner(), new AddUpdater());
        assertEquals(15, st.query(0, 5)); // 1+2+3+4+5
        st.update(0, 5, 2L); // +2 to all
        assertEquals(25, st.query(0, 5)); // 3+4+5+6+7
    }

    @Test
    void boundaryRanges() {
        Long[] a = {1L, 2L, 3L, 4L, 5L};
        SegmentTree<Long, Long> st = new SegmentTree<>(a, new SumCombiner(), new AddUpdater());
        assertEquals(1, st.query(0, 1)); // left boundary
        assertEquals(5, st.query(4, 5)); // right boundary
        st.update(0, 1, 3L); // +3 to left boundary
        assertEquals(4, st.query(0, 1));
        st.update(4, 5, 1L); // +1 to right boundary
        assertEquals(6, st.query(4, 5));
    }

    @Test
    void consecutiveUpdates() {
        Long[] a = {1L, 2L, 3L, 4L, 5L};
        SegmentTree<Long, Long> st = new SegmentTree<>(a, new SumCombiner(), new AddUpdater());
        st.update(1, 4, 2L); // +2 to 1,2,3
        st.update(2, 5, 3L); // +3 to 2,3,4
        assertEquals(30, st.query(0, 5)); // 1 + 4 + 8 + 9 + 8
    }

    @Test
    void randomOperations() {
        Random rnd = new Random(12345);
        int n = rnd.nextInt(1000) + 1;
        Long[] a = new Long[n];
        for (int i = 0; i < n; i++) {
            a[i] = (long) rnd.nextInt(100);
        }
        SumCombiner sumCombiner = new SumCombiner();
        AddUpdater addUpdater = new AddUpdater();
        SegmentTree<Long, Long> st = new SegmentTree<>(a, sumCombiner, addUpdater);
        NaiveArray<Long, Long> naive = new NaiveArray<>(a, sumCombiner, addUpdater);
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