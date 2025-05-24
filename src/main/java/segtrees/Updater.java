package segtrees;

/**
 * Interface for updating values in the segment tree.
 * @param <T> the type of the values to be updated
 * @param <U> the type of the update operation
 */
public interface Updater<T, U> {
    /**
     * Applies an update to a value over a range.
     * @param value the current value
     * @param update the update to apply
     * @param rangeSize the size of the range this value represents
     * @return the updated value
     */
    T applyUpdate(T value, U update, int rangeSize);

    /**
     * Composes two updates into a single update.
     * @param current the current update
     * @param next the next update to compose
     * @return the composed update
     */
    U composeUpdates(U current, U next);

    /**
     * Returns the neutral update element.
     * @return the neutral update
     */
    U neutralUpdate();
} 