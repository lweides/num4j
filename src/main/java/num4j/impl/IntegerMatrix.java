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

    IntegerMatrix(byte[] data, int... dimensions) {
        super(SPECIES, data, dimensions);
    }

    @Override
    protected Integer getDefaultValue() {
        return 0;
    }

    @Override
    protected Integer reduceLanes(Vector<Integer> vector, VectorOperators.Associative op) {
        return vector.reinterpretAsInts().reduceLanes(op);
    }

    @Override
    protected Integer add(Integer t1, Integer t2) {
        return t1 + t2;
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
}
