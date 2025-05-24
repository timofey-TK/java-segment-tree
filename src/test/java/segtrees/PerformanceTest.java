package segtrees;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PerformanceTest {

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
    void speedLarge() {
        int n = 1_000_000;
        int ops = 100_000;
        SegmentTree<Long, Long> st = new SegmentTree<>(n, new SumCombiner(), new AddUpdater());
        Random rnd = new Random(1);
        long t0 = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            if (rnd.nextBoolean()) {
                int l = rnd.nextInt(n);
                int r = rnd.nextInt(n - l) + l + 1;
                st.update(l, r, rnd.nextLong(1000));
            } else {
                int l = rnd.nextInt(n);
                int r = rnd.nextInt(n - l) + l + 1;
                st.query(l, r);
            }
        }
        long ms = (System.nanoTime() - t0) / 1_000_000;
        assertTrue(ms < 2000, "slow: " + ms + " ms");
    }
} 