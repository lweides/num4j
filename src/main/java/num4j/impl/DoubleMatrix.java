package num4j.impl;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;
import num4j.api.Matrix;
import num4j.unsafe.TheUnsafe;

public class DoubleMatrix extends InMemoryMatrix<Double> {

    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;
    /**
     * Creates a new double matrix with the specified {@code dimensions}, filled with {@code 0.0}.
     * @param dimensions dimensions of the matrix
     * @return the newly created matrix
     */
    public static DoubleMatrix zeros(int ... dimensions) {
        // we need to divide by 8, as elementSize() is in bits, not bytes
        int nrBytes = nrElements(dimensions) * (SPECIES.elementSize() / 8);
        byte[] data = new byte[nrBytes];
        return new DoubleMatrix(data, dimensions);
    }

    /**
     * Creates a new double matrix with the specified {@code dimensions}, filled with {@code 1.0}.
     * @param dimensions dimensions of the matrix
     * @return the newly created matrix
     */
    public static DoubleMatrix ones(int ... dimensions) {
        int nrElements = nrElements(dimensions);
        // we need to divide by 8, as elementSize() is in bits, not bytes
        int nrBytes = nrElements * (SPECIES.elementSize() / 8);
        byte[] data = new byte[nrBytes];
        for (int i = 0; i < nrElements; i++) {
            TheUnsafe.write(data, i, 1.0);
        }
        return new DoubleMatrix(data, dimensions);
    }

    public static DoubleMatrixBuilder builder() {
        return new DoubleMatrixBuilder();
    }

    DoubleMatrix(byte[] data, int... dimensions) {
        super(SPECIES, data, dimensions);
    }

    @Override
    protected Vector<Double> fromByteArray(byte[] data, int offset, VectorMask<Double> m) {
        return DoubleVector.fromByteArray(SPECIES, data, offset, BYTE_ORDER, m);
    }

    @Override
    protected Matrix<Double> createEmptyMatrix(int[] dimensions) {
        return zeros(dimensions);
    }

    @Override
    public void set(Double value, int address) {
        TheUnsafe.write(data(), address, value);
    }

    public static class DoubleMatrixBuilder extends AbstractBuilder<Double> {

        @Override
        protected void fill(int offset, byte[] data, Double... row) {
            for (int i = 0; i < row.length; i++) {
                TheUnsafe.write(data, offset + i, row[i]);
            }
        }

        @Override
        protected DoubleMatrix doBuild(byte[] data, int rows, int columns) {
            return new DoubleMatrix(data, rows, columns);
        }

        @Override
        protected int byteSize() {
            return Double.BYTES;
        }
    }
}
