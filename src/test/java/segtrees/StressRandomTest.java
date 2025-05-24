package segtrees;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StressRandomTest {

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

    @Test
    void stressSumAdd() {
        Random rnd = new Random(12345);
        int n = 1000;
        Long[] a = new Long[n];
        for (int i = 0; i < n; i++) {
            a[i] = (long) rnd.nextInt(100);
        }
        SumCombiner sumCombiner = new SumCombiner();
        AddUpdater addUpdater = new AddUpdater();
        SegmentTree<Long, Long> st = new SegmentTree<>(a, sumCombiner, addUpdater);
        NaiveArray<Long, Long> naive = new NaiveArray<>(a, sumCombiner, addUpdater);
        for (int i = 0; i < 10000; i++) {
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

    @Test
    void stressMinAssign() {
        Random rnd = new Random(12345);
        int n = 1000;
        Long[] a = new Long[n];
        for (int i = 0; i < n; i++) {
            a[i] = (long) rnd.nextInt(100);
        }
        MinCombiner minCombiner = new MinCombiner();
        AssignUpdater assignUpdater = new AssignUpdater();
        SegmentTree<Long, Long> st = new SegmentTree<>(a, minCombiner, assignUpdater);
        NaiveArray<Long, Long> naive = new NaiveArray<>(a, minCombiner, assignUpdater);
        for (int i = 0; i < 10000; i++) {
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
} 