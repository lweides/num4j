package num4j.api;

/**
 * Can be used to build matrices with at most 2 dimensions.
 */
public interface Builder<T extends Number> {

    /**
     * Adds the given values to the current {@link Matrix}.
     * Subsequent calls to this method are required to have the same size.
     * @param row numbers to be added to new row
     * @return the {@link Builder}
     */
    Builder<T> row(T... row);

    /**
     * After calling this method, the {@link Builder} may not be accessed again.
     * @return {@link Matrix} build by this {@link Builder}
     */
    Matrix<T> build();
}
