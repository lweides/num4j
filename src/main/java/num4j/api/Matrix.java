package num4j.api;

public interface Matrix<T extends Number> {

    /**
     * Adds the {@code other} {@link Matrix} to {@code this} elementwise and inplace.
     * @param other to be added
     * @throws num4j.exceptions.IncompatibleDimensionsException if the dimensions of the matrices do not match
     */
    void add(Matrix<T> other);

    /**
     * Transposes {@code this} by swapping dimensions via the permutation given in {@code swap}
     * @param swap dimension permutation
     * @return new transposed matrix
     */
    Matrix<T> transpose(int ... swap);

    /**
     * Sets the addressed {@code position} to the given {@code value}.
     * @param value new value
     * @param position position of new value
     * @throws num4j.exceptions.IncompatibleDimensionsException if the position cannot be addressed
     */
    void set(T value, int ... position);

    /**
     * @return the dimensions of the current {@link Matrix}
     */
    int[] dimensions();

    /**
     * @return the number of elements in the {@link Matrix}
     */
    int size();

    /**
     * @return the raw bytes of the elements contained in the {@link Matrix}
     */
    byte[] data();
}
