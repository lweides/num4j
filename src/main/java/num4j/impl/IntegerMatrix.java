package num4j.impl;

import jdk.incubator.vector.*;
import num4j.api.Matrix;
import num4j.unsafe.TheUnsafe;

public class IntegerMatrix extends InMemoryMatrix<Integer> {

    private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    /**
     * Creates a new int matrix with the specified {@code dimensions}, filled with {@code 0}.
     * @param dimensions dimensions of the matrix
     * @return the newly created matrix
     */
    public static IntegerMatrix zeros(int ... dimensions) {
        // we need to divide by 8, as elementSize() is in bits, not bytes
        int nrBytes = nrElements(dimensions) * (SPECIES.elementSize() / 8);
        byte[] data = new byte[nrBytes];
        return new IntegerMatrix(data, dimensions);
    }

    /**
     * Creates a new int matrix with the specified {@code dimensions}, filled with {@code 1}.
     * @param dimensions dimensions of the matrix
     * @return the newly created matrix
     */
    public static IntegerMatrix ones(int ... dimensions) {
        int nrElements = nrElements(dimensions);
        // we need to divide by 8, as elementSize() is in bits, not bytes
        int nrBytes = nrElements * (SPECIES.elementSize() / 8);
        byte[] data = new byte[nrBytes];
        for (int i = 0; i < nrElements; i++) {
            TheUnsafe.write(data, i, 1);
        }
        return new IntegerMatrix(data, dimensions);
    }

    public static IntegerMatrixBuilder builder() {
        return new IntegerMatrixBuilder();
    }

    IntegerMatrix(byte[] data, int... dimensions) {
        super(SPECIES, data, dimensions);
    }

    @Override
    protected Vector<Integer> fromByteArray(byte[] data, int offset, VectorMask<Integer> m) {
        return IntVector.fromByteArray(SPECIES, data, offset, BYTE_ORDER, m);
    }

    @Override
    protected Matrix<Integer> createEmptyMatrix(int[] dimensions) {
        return zeros(dimensions);
    }

    @Override
    protected void set(Integer value, int address) {
        TheUnsafe.write(data(), address, value);
    }

    public static class IntegerMatrixBuilder extends AbstractBuilder<Integer> {

        @Override
        protected void fill(int offset, byte[] data, Integer... row) {
            for (int i = 0; i < row.length; i++) {
                TheUnsafe.write(data, offset + i, row[i]);
            }
        }

        @Override
        protected IntegerMatrix doBuild(byte[] data, int rows, int columns) {
            return new IntegerMatrix(data, rows, columns);
        }

        @Override
        protected int byteSize() {
            return Integer.BYTES;
        }
    }
}
