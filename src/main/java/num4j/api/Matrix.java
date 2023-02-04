package num4j.api;

import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorMask;

import java.io.IOException;
import java.io.OutputStream;

public interface Matrix<T extends Number> {

    /**
     * Adds the {@code other} {@link Matrix} to {@code this} elementwise and inplace.
     * @param other to be added
     * @throws num4j.exceptions.IncompatibleDimensionsException if the dimensions of the matrices do not match
     */
    void add(Matrix<T> other);

    Matrix<T> mmul2D(Matrix<T> other);

    /**
     * Subtracts the {@code other} {@link Matrix} to {@code this} elementwise and inplace.
     * @param other to be subtracted
     * @throws num4j.exceptions.IncompatibleDimensionsException if the dimensions of the matrices do not match
     */
    void sub(Matrix<T> other);

    /**
     * Multiplies the {@code other} {@link Matrix} to {@code this} elementwise and inplace.
     * @param other to be multiplied by
     * @throws num4j.exceptions.IncompatibleDimensionsException if the dimensions of the matrices do not match
     */
    void mul(Matrix<T> other);

    /**
     * Divides the {@code other} {@link Matrix} to {@code this} elementwise and inplace.
     * @param other to be divided by
     * @throws num4j.exceptions.IncompatibleDimensionsException if the dimensions of the matrices do not match
     */
    void div(Matrix<T> other);

    /**
     * Transposes {@code this} by swapping dimensions via the permutation given in {@code swap}
     * @param swap dimension permutation
     * @return new transposed matrix
     */
    Matrix<T> transpose(int ... swap);

    /**
     * Sets the addressed {@code position} to the given {@code value}.
     * @param value new value
     * @param position position/start-address of new value
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

    /**
     * @return a deep copy
     */
    Matrix<T> copy();

    /**
     * @return vectorized representation of {@link #data()}
     */
    Vector<T> toVec(int offset, VectorMask<T> mask);

    /**
     * Writes the matrix to the specified {@link OutputStream}.
     * @param out stream to which the data is written
     */
    void write(OutputStream out) throws IOException;
}
