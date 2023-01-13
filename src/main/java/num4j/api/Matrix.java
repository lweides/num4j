package num4j.api;

public interface Matrix {

    /**
     * Adds the {@code other} {@link Matrix} to {@code this} elementwise and inplace.
     * @param other to be added
     * @throws num4j.exceptions.IncompatibleDimensionsException if the dimensions of the matrices do not match
     */
    void add(Matrix other);

    /**
     * Sets the addressed {@code position} to the given {@code value}.
     * @param value new value
     * @param position position of new value
     * @throws num4j.exceptions.IncompatibleDimensionsException if the position cannot be addressed
     */
    void set(double value, int ... position);

    /**
     * @return the dimensions of the current {@link Matrix}
     */
    int[] dimensions();

    /**
     * @return the number of elements in the {@link Matrix}
     */
    int size();

    /**
     * @return the elements contained in the {@link Matrix}
     */
    double[] data();
}
