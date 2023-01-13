package num4j.impl;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;
import num4j.api.Matrix;
import num4j.exceptions.IncompatibleDimensionsException;

import java.nio.ByteOrder;
import java.util.Arrays;

abstract class InMemoryMatrix<T extends Number> implements Matrix<T> {

    protected static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;


    /**
     * @return number of elements a matrix with the specified {@code dimensions} contains.
     */
    protected static int nrElements(int ... dimensions) {
        return Arrays.stream(dimensions)
            .reduce((i1, i2) -> i1 * i2)
            .orElse(0);
    }

    private static void ensureValidDimensions(int elementSize, byte[] data, int ... dimensions) {
        int nrElements = Arrays.stream(dimensions)
            .reduce((i1, i2) -> i1 * i2)
            .orElse(0);

        if (data.length != nrElements * elementSize / 8) {
            throw new IncompatibleDimensionsException("Dimensions do not fit in data");
        }
    }

    private final VectorSpecies<T> species;
    private final byte[] data;
    private final int[] dimensions;

    InMemoryMatrix(VectorSpecies<T> species, byte[] data, int ... dimensions) {
        ensureValidDimensions(species.elementSize(), data, dimensions);
        this.species = species;
        this.data = data;
        this.dimensions = dimensions;
    }

    @Override
    public void add(Matrix<T> other) {
        ensureSameDimensions(other);
        int upperBound = data.length / elementSize();

        for (int offset = 0; offset < upperBound; offset += species.length()) {
            VectorMask<T> mask = species.indexInRange(offset, upperBound);
            Vector<T> va = fromByteArray(data, offset, mask);
            Vector<T> vo = fromByteArray(other.data(), offset, mask);

            va = va.add(vo);
            va.intoByteArray(data, offset, BYTE_ORDER, mask);
        }
    }

    protected abstract Vector<T> fromByteArray(byte[] data, int offset, VectorMask<T> m);

    protected int elementSize() {
        // divide by 8, as elementSize() is in bits, not bytes
        return species.elementSize() / 8;
    }

    @Override
    public void set(T value, int... position) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public int[] dimensions() {
        return dimensions;
    }

    @Override
    public int size() {
        return data.length * 8 / species.elementSize();
    }

    @Override
    public byte[] data() {
        return data;
    }

    private <O extends Number> void ensureSameDimensions(Matrix<O> other) {
        if (!Arrays.equals(dimensions, other.dimensions())) {
            throw new IncompatibleDimensionsException("Dimensions do not match");
        }
    }
}
