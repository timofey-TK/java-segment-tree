package segtrees;

/**
 * Interface for combining values in the segment tree.
 * @param <T> the type of the values to be combined
 */
public interface Combiner<T> {
    /**
     * Combines two values into a single value.
     * @param left the left value
     * @param right the right value
     * @return the combined value
     */
    T combine(T left, T right);

    /**
     * Returns the neutral element for the combine operation.
     * @return the neutral element
     */
    T neutral();
} 